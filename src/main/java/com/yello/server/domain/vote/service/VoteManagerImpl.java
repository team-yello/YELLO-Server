package com.yello.server.domain.vote.service;

import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.keyword.dto.response.KeywordCheckResponse;
import com.yello.server.domain.keyword.entity.Keyword;
import com.yello.server.domain.question.dto.response.QuestionForVoteResponse;
import com.yello.server.domain.question.dto.response.QuestionVO;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.user.service.UserManager;
import com.yello.server.domain.vote.dto.request.VoteAnswer;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.exception.VoteForbiddenException;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import com.yello.server.domain.vote.repository.VoteRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static com.yello.server.global.common.ErrorCode.*;
import static com.yello.server.global.common.factory.WeightedRandomFactory.randomPoint;
import static com.yello.server.global.common.util.ConstantUtil.*;

@Builder
@Component
@RequiredArgsConstructor
@Transactional
public class VoteManagerImpl implements VoteManager {

    public final static String GREETING_NAME_FOOT = "에게 옐로가 전할 말은";
    public final static String GREETING_KEYWORD_FOOT = "라는 말이야";

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final VoteRepository voteRepository;
    private final FriendRepository friendRepository;

    private final UserManager userManager;

    @Override
    public List<Vote> createVotes(Long senderId, List<VoteAnswer> voteAnswers) {
        List<Vote> votes = new ArrayList<>();
        final User sender = userRepository.getById(senderId);

        IntStream.range(0, voteAnswers.size())
                .filter(index -> {
                    User receiver = userRepository.getById(voteAnswers.get(index).friendId());
                    return Objects.isNull(receiver.getDeletedAt());
                })
                .forEach(index -> {
                    VoteAnswer currentVote = voteAnswers.get(index);

                    if (isDuplicatedVote(index, voteAnswers)) {
                        throw new VoteForbiddenException(DUPLICATE_VOTE_EXCEPTION);
                    }

                    User receiver = userRepository.getById(currentVote.friendId());
                    Question question = questionRepository.getById(currentVote.questionId());


                    Vote newVote = Vote.createVote(
                            currentVote.keywordName(),
                            sender,
                            receiver,
                            question,
                            currentVote.colorIndex()
                    );

                    Vote savedVote = voteRepository.save(newVote);
                    votes.add(savedVote);
                });

        return votes;
    }

    @Override
    public List<QuestionForVoteResponse> generateVoteQuestion(User user, List<Question> questions) {
        List<Question> questionList = new ArrayList<>(questions);
        Collections.shuffle(questionList);

        return questionList.stream()
                .map(question -> QuestionForVoteResponse.builder()
                        .friendList(getShuffledFriends(user))
                        .keywordList(getShuffledKeywords(question))
                        .question(QuestionVO.of(question))
                        .questionPoint(randomPoint())
                        .subscribe(user.getSubscribe().toString())
                        .build())
                .limit(VOTE_COUNT)
                .toList();
    }

    @Override
    public int useNameHint(User sender, Vote vote) {
        if (sender.getPoint() < NAME_HINT_POINT && sender.getSubscribe() == Subscribe.NORMAL) {
            throw new VoteForbiddenException(LACK_POINT_EXCEPTION);
        }

        if (vote.getNameHint() != NAME_HINT_DEFAULT) {
            throw new VoteNotFoundException(INVALID_VOTE_EXCEPTION);
        }

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        int randomIndex = random.nextInt(2);
        vote.checkNameIndexOf(randomIndex);

        if (sender.getSubscribe() == Subscribe.NORMAL) {
            sender.subPoint(NAME_HINT_POINT);
            return randomIndex;
        }
        sender.subPoint(0);
        return randomIndex;
    }

    @Override
    public KeywordCheckResponse useKeywordHint(User user, Vote vote) {
        if (user.getPoint() < KEYWORD_HINT_POINT) {
            throw new VoteForbiddenException(LACK_POINT_EXCEPTION);
        }

        user.subPoint(KEYWORD_HINT_POINT);
        vote.checkKeyword();

        return KeywordCheckResponse.of(vote);
    }

    @Override
    public void makeGreetingVote(User user) {
        final User sender = userManager.getOfficialUser(user.getGender());
        final Question greetingQuestion = questionRepository.findByQuestionContent(
                null,
                GREETING_NAME_FOOT,
                null,
                GREETING_KEYWORD_FOOT
        ).orElseGet(() ->
                questionRepository.save(
                        Question.of(
                                null,
                                GREETING_NAME_FOOT,
                                null,
                                GREETING_KEYWORD_FOOT)
                )
        );

        voteRepository.save(createFirstVote(sender, user, greetingQuestion));
    }

    @Override
    public List<FriendShuffleResponse> getShuffledFriends(User user) {
        List<String> uuidList = Arrays.asList(YELLO_FEMALE, YELLO_MALE);
        final List<Friend> friends = friendRepository.findAllByUserIdNotIn(user.getId(), uuidList);

        List<Friend> friendList = new ArrayList<>(friends);
        Collections.shuffle(friendList);

        if (friends.size() == NO_FRIEND_COUNT) {
            throw new FriendException(LACK_USER_EXCEPTION);
        }

        if (friends.size() > NO_FRIEND_COUNT && friends.size() < RANDOM_COUNT) {
            return friendList.stream()
                    .map(FriendShuffleResponse::of)
                    .toList();
        }

        return friendList.stream()
                .map(FriendShuffleResponse::of)
                .limit(RANDOM_COUNT)
                .toList();
    }

    private boolean isDuplicatedVote(int index, List<VoteAnswer> voteAnswers) {
        return index > 0 && voteAnswers.get(index - 1).questionId()
                .equals(voteAnswers.get(index).questionId());
    }

    private List<String> getShuffledKeywords(Question question) {
        final List<Keyword> keywords = question.getKeywordList();
        List<Keyword> keywordList = new ArrayList<>(keywords);
        Collections.shuffle(keywordList);

        return keywordList.stream()
                .map(Keyword::getKeywordName)
                .limit(RANDOM_COUNT)
                .toList();
    }

    private Vote createFirstVote(User sender, User receiver, Question question) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final String answer = "널 기다렸어";

        return Vote.builder()
                .answer(answer)
                .nameHint(-3)
                .isAnswerRevealed(true)
                .isRead(false)
                .sender(sender)
                .receiver(receiver)
                .question(question)
                .colorIndex(random.nextInt(12) + 1)
                .build();
    }
}
