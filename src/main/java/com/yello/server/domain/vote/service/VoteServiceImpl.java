package com.yello.server.domain.vote.service;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.entity.CooldownRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.keyword.entity.Keyword;
import com.yello.server.domain.question.dto.response.YelloFriend;
import com.yello.server.domain.question.dto.response.YelloQuestion;
import com.yello.server.domain.question.dto.response.YelloStartResponse;
import com.yello.server.domain.question.dto.response.YelloVoteResponse;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.entity.QuestionRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.vote.dto.response.KeywordCheckResponse;
import com.yello.server.domain.vote.dto.response.VoteDetailResponse;
import com.yello.server.domain.vote.dto.response.VoteFriendResponse;
import com.yello.server.domain.vote.dto.response.VoteResponse;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.entity.VoteRepository;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import com.yello.server.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.yello.server.domain.vote.common.WeightedRandom.randomPoint;
import static com.yello.server.global.common.ErrorCode.NOT_FOUND_VOTE_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.*;
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

    @Override
    public List<VoteResponse> findAllVotes(Pageable pageable) {
        //todo User
        return voteRepository.findAll(pageable)
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
    public List<VoteFriendResponse> findAllFriendVotes(Pageable pageable) {
        return null;
    }

    @Transactional
    @Override
    public KeywordCheckResponse checkKeyword(Long userId, Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new VoteNotFoundException(NOT_FOUND_VOTE_EXCEPTION));
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.NOT_FOUND_USER_EXCEPTION));

        vote.updateKeywordCheck();

        return KeywordCheckResponse.of(vote);
    }

    @Override
    public List<YelloVoteResponse> findYelloVoteList(Long userId) {

        List<YelloVoteResponse> yelloVoteList = new ArrayList<>();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER_EXCEPTION));

        List<Question> question = questionRepository.findAll();
        Collections.shuffle(question);

        List<Question> yelloQuestionList = question.stream()
                .limit(VOTE_COUNT).toList();

        yelloQuestionList.forEach(yello -> yelloVoteList.add(getVoteData(user, yello)));


        return yelloVoteList;
    }

    @Override
    public YelloStartResponse checkVoteAvailable(Long userId) {
        boolean isStart = true;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER_EXCEPTION));

        List<Friend> friends = friendRepository.findAllByUser(user);

        if (friends.size() < RANDOM_COUNT)
            throw new UserNotFoundException(ErrorCode.LACK_USER_EXCEPTION);

        Cooldown cooldown = cooldownRepository.findByUser(user)
                .orElse(Cooldown.builder().user(user).createdAt(null).build());

        // 40분 지난 경우 투표 시작
        if (cooldown.getCreatedAt() != null && timeDiff(cooldown.getCreatedAt()) < TIMER_TIME) {
            isStart = false;
        }

        return YelloStartResponse.builder()
                .isStart(isStart)
                .point(user.getPoint())
                .createdAt(toDateFormattedString(cooldown.getCreatedAt()))
                .build();
    }

    public YelloVoteResponse getVoteData(User user, Question question) {
        List<Keyword> keywordList = question.getKeywordList();
        Collections.shuffle(keywordList);

        return YelloVoteResponse.builder()
                .friendList(getFriendList(user))
                .keywordList(getKeywordList(question))
                .question(YelloQuestion.of(question))
                .questionPoint(randomPoint())
                .build();
    }

    public List<YelloFriend> getFriendList(User user) {
        List<Friend> allFriend = friendRepository.findAllByUser(user);
        Collections.shuffle(allFriend);

        return allFriend.stream()
                .map(YelloFriend::of)
                .limit(RANDOM_COUNT)
                .collect(Collectors.toList());
    }

    public List<String> getKeywordList(Question question) {
        List<Keyword> keywordList = question.getKeywordList();
        Collections.shuffle(keywordList);

        return keywordList.stream()
                .map(Keyword::getKeywordName)
                .limit(RANDOM_COUNT)
                .collect(Collectors.toList());
    }

    public long timeDiff(LocalDateTime localDateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Duration duration = Duration.between(localDateTime, currentDateTime);
        return duration.getSeconds();
    }

    private Vote findVote(Long id) {
        return voteRepository.findById(id)
                .orElseThrow(() -> new VoteNotFoundException(NOT_FOUND_VOTE_EXCEPTION));
    }
}
