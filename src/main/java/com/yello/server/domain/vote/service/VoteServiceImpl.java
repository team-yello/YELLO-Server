package com.yello.server.domain.vote.service;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.entity.CooldownRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.keyword.entity.Keyword;
import com.yello.server.domain.question.dto.response.VoteAvailableResponse;
import com.yello.server.domain.question.dto.response.VoteContentVO;
import com.yello.server.domain.question.dto.response.VoteQuestionResponse;
import com.yello.server.domain.question.dto.response.VoteShuffleFriend;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.entity.QuestionRepository;
import com.yello.server.domain.question.service.QuestionService;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.service.UserService;
import com.yello.server.domain.vote.dto.request.CreateVoteRequest;
import com.yello.server.domain.vote.dto.response.*;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.entity.VoteRepository;
import com.yello.server.domain.vote.exception.VoteForbiddenException;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import com.yello.server.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.yello.server.domain.vote.common.WeightedRandom.randomPoint;
import static com.yello.server.global.common.ErrorCode.*;
import static com.yello.server.global.common.util.ConstantUtil.*;
import static com.yello.server.global.common.util.TimeUtil.timeDiff;
import static com.yello.server.global.common.util.TimeUtil.toDateFormattedString;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteServiceImpl implements VoteService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final QuestionRepository questionRepository;
    private final CooldownRepository cooldownRepository;
    private final VoteRepository voteRepository;
    private final UserService userService;
    private final QuestionService questionService;

    public List<VoteResponse> findAllVotes(Long userId, Pageable pageable) {
        return voteRepository.findAllByReceiverUserId(userId, pageable)
                .stream()
                .map(VoteResponse::of)
                .toList();
    }

    @Override
    public VoteDetailResponse findVoteById(Long id) {
        Vote vote = findVote(id);
        return VoteDetailResponse.of(vote);
    }

    @Override
    public List<VoteFriendResponse> findAllFriendVotes(Long userId, Pageable pageable) {
        //todo 후순위
        return null;
    }

    @Transactional
    @Override
    public KeywordCheckResponse checkKeyword(Long userId, Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new VoteNotFoundException(NOT_FOUND_VOTE_EXCEPTION));

        findUser(userId);
        vote.updateKeywordCheck();

        return KeywordCheckResponse.of(vote);
    }

    @Override
    public List<VoteQuestionResponse> findYelloVoteList(Long userId) {

        List<VoteQuestionResponse> yelloVoteList = new ArrayList<>();
        User user = findUser(userId);

        List<Question> question = questionRepository.findAll();
        Collections.shuffle(question);

        List<Question> yelloQuestionList = question.stream()
                .limit(VOTE_COUNT).toList();

        yelloQuestionList.forEach(yello -> yelloVoteList.add(getVoteData(user, yello)));

        return yelloVoteList;
    }

    @Override
    public VoteAvailableResponse checkVoteAvailable(Long userId) {
        boolean isStart = true;
        User user = findUser(userId);
        List<Friend> friends = friendRepository.findAllByUser(user);

        if (friends.size() < RANDOM_COUNT) {
            throw new UserNotFoundException(LACK_USER_EXCEPTION);
        }

        Cooldown cooldown = cooldownRepository.findByUser(user)
                .orElse(Cooldown.builder().user(user).createdAt(null).build());

        // 40분 지난 경우 투표 시작
        if (cooldown.getCreatedAt() != null && timeDiff(cooldown.getCreatedAt()) < TIMER_TIME) {
            isStart = false;
        }

        return VoteAvailableResponse.builder()
                .isStart(isStart)
                .point(user.getPoint())
                .createdAt(toDateFormattedString(cooldown.getCreatedAt()))
                .build();
    }

    @Transactional
    @Override
    public void createVote(Long userId, CreateVoteRequest request) {
        User sender = findUser(userId);
        sender.plusPoint(request.totalPoint());

        request.voteAnswerList().forEach(vote ->
                voteRepository.save(Vote.createVote(vote.keywordName(), sender, findUser(vote.friendId()),
                        questionService.findByQuestionId(vote.questionId()), vote.colorIndex())));
    }

    @Transactional
    @Override
    public RevealNameResponse revealNameHint(Long userId, Long voteId) {
        User sender = findUser(userId);
        Vote vote = findVote(voteId);
        String name = vote.getSender().getName();

        if (vote.getNameHint() != NAME_HINT_DEFAULT) {
            throw new VoteNotFoundException(ErrorCode.INVALID_VOTE_EXCEPTION);
        }
        if (sender.getPoint() < NAME_HINT_POINT) {
            throw new VoteForbiddenException(ErrorCode.LACK_POINT_EXCEPTION);
        }
        int randomIndex = (int) (Math.random() * 2);

        vote.updateNameHintReveal(randomIndex);
        sender.minusPoint(NAME_HINT_POINT);

        return RevealNameResponse.builder()
                .name(name.charAt(randomIndex))
                .build();
    }

    public VoteQuestionResponse getVoteData(User user, Question question) {
        List<Keyword> keywordList = question.getKeywordList();
        Collections.shuffle(keywordList);

        return VoteQuestionResponse.builder()
                .friendList(getFriendList(user))
                .keywordList(getKeywordList(question))
                .question(VoteContentVO.of(question))
                .questionPoint(randomPoint())
                .build();
    }

    public List<VoteShuffleFriend> getFriendList(User user) {
        List<Friend> allFriend = friendRepository.findAllByUser(user);
        Collections.shuffle(allFriend);

        return allFriend.stream()
                .map(VoteShuffleFriend::of)
                .limit(RANDOM_COUNT)
                .collect(Collectors.toList());
    }

    public List<String> getKeywordList(Question question) {
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
}
