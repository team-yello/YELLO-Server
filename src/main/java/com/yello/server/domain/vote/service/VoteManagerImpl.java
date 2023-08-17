package com.yello.server.domain.vote.service;

import static com.yello.server.domain.vote.common.WeightedRandomFactory.randomPoint;
import static com.yello.server.global.common.ErrorCode.DUPLICATE_VOTE_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.INVALID_VOTE_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.LACK_POINT_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.KEYWORD_HINT_POINT;
import static com.yello.server.global.common.util.ConstantUtil.NAME_HINT_DEFAULT;
import static com.yello.server.global.common.util.ConstantUtil.NAME_HINT_POINT;
import static com.yello.server.global.common.util.ConstantUtil.RANDOM_COUNT;
import static com.yello.server.global.common.util.ConstantUtil.VOTE_COUNT;

import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.keyword.dto.response.KeywordCheckResponse;
import com.yello.server.domain.keyword.entity.Keyword;
import com.yello.server.domain.question.dto.response.QuestionForVoteResponse;
import com.yello.server.domain.question.dto.response.QuestionVO;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.user.service.UserManager;
import com.yello.server.domain.vote.dto.request.VoteAnswer;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.exception.VoteForbiddenException;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import com.yello.server.domain.vote.repository.VoteRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class VoteManagerImpl implements VoteManager {

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
            .forEach(index -> {
                VoteAnswer currentVote = voteAnswers.get(index);

                if (isDuplicatedVote(index, voteAnswers)) {
                    throw new VoteForbiddenException(DUPLICATE_VOTE_EXCEPTION);
                }

                User receiver = userRepository.getById(currentVote.friendId());
                Question question = questionRepository.findById(currentVote.questionId());

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
        Collections.shuffle(Arrays.asList(questions));

        return questions.stream()
            .map(question -> {
                final List<Keyword> keywordList = question.getKeywordList();
                Collections.shuffle(Arrays.asList(keywordList));

                return QuestionForVoteResponse.builder()
                    .friendList(getShuffledFriends(user))
                    .keywordList(getShuffledKeywords(question))
                    .question(QuestionVO.of(question))
                    .questionPoint(randomPoint())
                    .subscribe(user.getSubscribe().toString())
                    .build();
            })
            .limit(VOTE_COUNT)
            .toList();
    }

    @Override
    public int useNameHint(User sender, Vote vote) {
        if (sender.getPoint() < NAME_HINT_POINT) {
            throw new VoteForbiddenException(LACK_POINT_EXCEPTION);
        }

        if (vote.getNameHint()!=NAME_HINT_DEFAULT) {
            throw new VoteNotFoundException(INVALID_VOTE_EXCEPTION);
        }

        int randomIndex = (int) (Math.random() * 2);
        vote.checkNameIndexOf(randomIndex);
        sender.minusPoint(NAME_HINT_POINT);
        return randomIndex;
    }

    @Override
    public KeywordCheckResponse useKeywordHint(User user, Vote vote) {
        if (user.getPoint() < KEYWORD_HINT_POINT) {
            throw new VoteForbiddenException(LACK_POINT_EXCEPTION);
        }

        user.minusPoint(KEYWORD_HINT_POINT);
        vote.checkKeyword();
        return KeywordCheckResponse.of(vote);
    }

    @Override
    public void makeGreetingVote(User user) {
        final String greetingNameFoot = "에게 옐로가 전할 말은";
        final String greetingKeywordFoot = "라는 말이야";

        final User sender = userManager.getOfficialUser(user.getGender());

        final Question greetingQuestion = questionRepository.findByQuestionContent(
            null,
            greetingNameFoot,
            null,
            greetingKeywordFoot
        ).orElseGet(() ->
            questionRepository.save(
                Question.of(
                    null,
                    greetingNameFoot,
                    null,
                    greetingKeywordFoot)
            )
        );

        voteRepository.save(createFirstVote(sender, user, greetingQuestion));
    }

    private boolean isDuplicatedVote(int index, List<VoteAnswer> voteAnswers) {
        return index > 0 && voteAnswers.get(index - 1).questionId()
            .equals(voteAnswers.get(index).questionId());
    }

    private List<FriendShuffleResponse> getShuffledFriends(User user) {
        final List<Friend> allFriend = friendRepository.findAllByUserId(user.getId());
        Collections.shuffle(Arrays.asList(allFriend));

        return allFriend.stream()
            .map(FriendShuffleResponse::of)
            .limit(RANDOM_COUNT)
            .toList();
    }

    private List<String> getShuffledKeywords(Question question) {
        final List<Keyword> keywordList = question.getKeywordList();
        Collections.shuffle(keywordList);

        return keywordList.stream()
            .map(Keyword::getKeywordName)
            .limit(RANDOM_COUNT)
            .toList();
    }

    private Vote createFirstVote(User sender, User receiver, Question question) {
        Random random = new Random();
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
