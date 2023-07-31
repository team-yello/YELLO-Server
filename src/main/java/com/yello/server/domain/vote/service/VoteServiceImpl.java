package com.yello.server.domain.vote.service;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.entity.CooldownRepository;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.keyword.dto.response.KeywordCheckResponse;
import com.yello.server.domain.keyword.entity.Keyword;
import com.yello.server.domain.question.dto.response.QuestionForVoteResponse;
import com.yello.server.domain.question.dto.response.QuestionVO;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.entity.QuestionRepository;
import com.yello.server.domain.question.exception.QuestionException;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.domain.vote.dto.request.CreateVoteRequest;
import com.yello.server.domain.vote.dto.request.VoteAnswer;
import com.yello.server.domain.vote.dto.response.*;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.entity.VoteRepository;
import com.yello.server.domain.vote.exception.VoteForbiddenException;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.yello.server.domain.vote.common.WeightedRandomFactory.randomPoint;
import static com.yello.server.global.common.ErrorCode.*;
import static com.yello.server.global.common.ErrorCode.DUPLICATE_VOTE_EXCEPTION;
import static com.yello.server.global.common.factory.TimeFactory.minusTime;
import static com.yello.server.global.common.util.ConstantUtil.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteServiceImpl implements VoteService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final QuestionRepository questionRepository;
    private final CooldownRepository cooldownRepository;
    private final VoteRepository voteRepository;

    public VoteListResponse findAllVotes(Long userId, Pageable pageable) {
        Integer count = voteRepository.countAllByReceiverUserId(userId);
        List<VoteResponse> votes = voteRepository.findAllByReceiverUserId(userId, pageable)
                .stream()
                .map(VoteResponse::of)
                .toList();
        return VoteListResponse.of(count, votes);
    }

    @Transactional
    @Override
    public VoteDetailResponse findVoteById(Long id) {
        Vote vote = findVote(id);
        vote.read();
        return VoteDetailResponse.of(vote);
    }

    @Override
    public List<VoteFriendResponse> findAllFriendVotes(Long userId, Pageable pageable) {
        return voteRepository.findAllReceivedByFriends(userId, pageable)
                .stream()
                .map(VoteFriendResponse::of)
                .toList();
    }

    @Transactional
    @Override
    public KeywordCheckResponse checkKeyword(Long userId, Long voteId) {
        Vote vote = findVote(voteId);
        User user = findUser(userId);

        vote.checkKeyword();

        if (user.getPoint() < KEYWORD_HINT_POINT) {
            throw new VoteForbiddenException(LACK_POINT_EXCEPTION);
        }

        user.minusPoint(KEYWORD_HINT_POINT);
        return KeywordCheckResponse.of(vote);
    }

    @Override
    public List<QuestionForVoteResponse> findVoteQuestionList(Long userId) {
        User user = findUser(userId);

        List<Friend> friends = friendRepository.findAllByUserId(user.getId());
        if (friends.size() < RANDOM_COUNT) {
            throw new FriendException(LACK_USER_EXCEPTION);
        }

        List<Question> questions = questionRepository.findAll();
        Collections.shuffle(questions);

        List<Question> questionList = questions.stream()
                .limit(VOTE_COUNT)
                .toList();

        return questionList.stream()
                .map(question -> generateVoteQuestion(user, question))
                .toList();
    }

    @Override
    public VoteAvailableResponse checkVoteAvailable(Long userId) {
        User user = findUser(userId);
        List<Friend> friends = friendRepository.findAllByUserId(user.getId());

        if (friends.size() < RANDOM_COUNT) {
            throw new FriendException(LACK_USER_EXCEPTION);
        }

        Cooldown cooldown = cooldownRepository.findByUserId(user.getId())
                .orElse(Cooldown.of(user, minusTime(LocalDateTime.now(), COOL_DOWN_TIME)));

        return VoteAvailableResponse.of(user, cooldown);
    }

    @Transactional
    @Override
    public VoteCreateResponse createVote(Long userId, CreateVoteRequest request) {
        User sender = findUser(userId);
        sender.plusPoint(request.totalPoint());
        List<VoteAnswer> voteAnswerList = request.voteAnswerList();

        IntStream.range(0, voteAnswerList.size())
                .forEach(index -> {
                    if (index > 0 && voteAnswerList.get(index - 1).questionId() == voteAnswerList.get(index).questionId()) {
                        throw new VoteForbiddenException(DUPLICATE_VOTE_EXCEPTION);
                    }

                    User receiver = findUser(voteAnswerList.get(index).friendId());
                    Question question = findQuestion(voteAnswerList.get(index).questionId());
                    Vote newVote = Vote.createVote(voteAnswerList.get(index).keywordName(), sender, receiver, question, voteAnswerList.get(index).colorIndex());
                    voteRepository.save(newVote);
                });

        Optional<Cooldown> cooldown = cooldownRepository.findByUserId(sender.getId());
        if (cooldown.isEmpty()) {
            cooldownRepository.save(Cooldown.of(sender, LocalDateTime.now()));
        } else {
            cooldown.get().updateDate(LocalDateTime.now());
        }

        return VoteCreateResponse.builder().point(sender.getPoint()).build();
    }

    @Transactional
    @Override
    public RevealNameResponse revealNameHint(Long userId, Long voteId) {
        User sender = findUser(userId);
        if (sender.getPoint() < NAME_HINT_POINT) {
            throw new VoteForbiddenException(LACK_POINT_EXCEPTION);
        }

        Vote vote = findVote(voteId);
        if (vote.getNameHint() != NAME_HINT_DEFAULT) {
            throw new VoteNotFoundException(INVALID_VOTE_EXCEPTION);
        }

        int randomIndex = (int) (Math.random() * 2);
        vote.checkKeywordIndexOf(randomIndex);
        sender.minusPoint(NAME_HINT_POINT);

        return RevealNameResponse.of(vote.getSender(), randomIndex);
    }

    private QuestionForVoteResponse generateVoteQuestion(User user, Question question) {
        List<Keyword> keywordList = question.getKeywordList();
        Collections.shuffle(keywordList);

        return QuestionForVoteResponse.builder()
                .friendList(getShuffledFriends(user))
                .keywordList(getShuffledKeywords(question))
                .question(QuestionVO.of(question))
                .questionPoint(randomPoint())
                .build();
    }

    private List<FriendShuffleResponse> getShuffledFriends(User user) {
        List<Friend> allFriend = friendRepository.findAllByUserId(user.getId());
        Collections.shuffle(allFriend);

        return allFriend.stream()
                .map(FriendShuffleResponse::of)
                .limit(RANDOM_COUNT)
                .toList();
    }

    private List<String> getShuffledKeywords(Question question) {
        List<Keyword> keywordList = question.getKeywordList();
        Collections.shuffle(keywordList);

        return keywordList.stream()
                .map(Keyword::getKeywordName)
                .limit(RANDOM_COUNT)
                .toList();
    }

    private Vote findVote(Long id) {
        return voteRepository.findById(id)
                .orElseThrow(() -> new VoteNotFoundException(NOT_FOUND_VOTE_EXCEPTION));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USERID_NOT_FOUND_USER_EXCEPTION));
    }

    private Question findQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionException(NOT_FOUND_QUESTION_EXCEPTION));
    }

}