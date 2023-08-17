package com.yello.server.domain.vote.service;

import static com.yello.server.global.common.ErrorCode.LACK_USER_EXCEPTION;
import static com.yello.server.global.common.factory.TimeFactory.minusTime;
import static com.yello.server.global.common.util.ConstantUtil.COOL_DOWN_TIME;
import static com.yello.server.global.common.util.ConstantUtil.RANDOM_COUNT;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.keyword.dto.response.KeywordCheckResponse;
import com.yello.server.domain.keyword.repository.KeywordRepository;
import com.yello.server.domain.question.dto.response.QuestionForVoteResponse;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.dto.request.CreateVoteRequest;
import com.yello.server.domain.vote.dto.response.RevealNameResponse;
import com.yello.server.domain.vote.dto.response.VoteAvailableResponse;
import com.yello.server.domain.vote.dto.response.VoteCreateVO;
import com.yello.server.domain.vote.dto.response.VoteDetailResponse;
import com.yello.server.domain.vote.dto.response.VoteFriendResponse;
import com.yello.server.domain.vote.dto.response.VoteFriendVO;
import com.yello.server.domain.vote.dto.response.VoteListResponse;
import com.yello.server.domain.vote.dto.response.VoteResponse;
import com.yello.server.domain.vote.dto.response.VoteUnreadCountResponse;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.infrastructure.rabbitmq.service.ProducerService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Builder
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final QuestionRepository questionRepository;
    private final CooldownRepository cooldownRepository;
    private final VoteRepository voteRepository;
    private final KeywordRepository keywordRepository;

    private final VoteManager voteManager;
    private final ProducerService producerService;

    public VoteListResponse findAllVotes(Long userId, Pageable pageable) {
        final Integer count = voteRepository.countAllByReceiverUserId(userId);
        final User user = userRepository.getById(userId);
        final List<VoteResponse> votes = voteRepository.findAllByReceiverUserId(userId, pageable)
            .stream()
            .map(VoteResponse::of)
            .toList();
        return VoteListResponse.of(count, votes, user);
    }

    public VoteUnreadCountResponse getUnreadVoteCount(Long userId) {
        final Integer count = voteRepository.countUnreadByReceiverUserId(userId);
        return VoteUnreadCountResponse.of(count);
    }

    @Transactional
    public VoteDetailResponse findVoteById(Long voteId, Long userId) {
        final Vote vote = voteRepository.getById(voteId);
        final User user = userRepository.getById(userId);

        vote.read();
        return VoteDetailResponse.of(vote, user);
    }

    public VoteFriendResponse findAllFriendVotes(Long userId, Pageable pageable) {
        final Integer totalCount = voteRepository.countAllReceivedByFriends(userId);
        final List<VoteFriendVO> list = voteRepository.findAllReceivedByFriends(userId, pageable)
            .stream()
            .filter(vote -> vote.getNameHint()!=-3)
            .map(VoteFriendVO::of)
            .toList();
        return VoteFriendResponse.of(totalCount, list);
    }

    @Transactional
    public KeywordCheckResponse checkKeyword(Long userId, Long voteId) {
        final Vote vote = voteRepository.getById(voteId);
        final User user = userRepository.getById(userId);

        return voteManager.useKeywordHint(user, vote);
    }

    public List<QuestionForVoteResponse> findVoteQuestionList(Long userId) {
        final User user = userRepository.getById(userId);

        final List<Friend> friends = friendRepository.findAllByUserId(user.getId());
        if (friends.size() < RANDOM_COUNT) {
            throw new FriendException(LACK_USER_EXCEPTION);
        }

        final List<Question> questions = questionRepository.findAll();
        return voteManager.generateVoteQuestion(user, questions);
    }

    public VoteAvailableResponse checkVoteAvailable(Long userId) {
        final User user = userRepository.getById(userId);
        final List<Friend> friends = friendRepository.findAllByUserId(user.getId());

        if (friends.size() < RANDOM_COUNT) {
            throw new FriendException(LACK_USER_EXCEPTION);
        }

        final Cooldown cooldown = cooldownRepository.findByUserId(user.getId())
            .orElse(Cooldown.of(user, minusTime(LocalDateTime.now(), COOL_DOWN_TIME)));

        return VoteAvailableResponse.of(user, cooldown);
    }

    @Transactional
    public VoteCreateVO createVote(Long userId, CreateVoteRequest request) {
        final User sender = userRepository.getById(userId);
        List<Vote> votes = voteManager.createVotes(userId, request.voteAnswerList());

        Cooldown cooldown = cooldownRepository.findByUserId(sender.getId())
            .orElseGet(() -> cooldownRepository.save(Cooldown.of(sender, LocalDateTime.now())));

        cooldown.updateDate(LocalDateTime.now());
        producerService.produceVoteAvailableNotification(cooldown);

        sender.plusPoint(request.totalPoint());
        return VoteCreateVO.of(sender.getPoint(), votes);
    }

    @Transactional
    public RevealNameResponse revealNameHint(Long userId, Long voteId) {
        final User sender = userRepository.getById(userId);
        final Vote vote = voteRepository.getById(voteId);

        int randomIndex = voteManager.useNameHint(sender, vote);
        return RevealNameResponse.of(vote.getSender(), randomIndex);
    }
}