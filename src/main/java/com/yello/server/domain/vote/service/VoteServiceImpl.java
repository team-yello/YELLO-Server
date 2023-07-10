package com.yello.server.domain.vote.service;

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
import com.yello.server.global.common.ErrorCode;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_VOTE_EXCEPTION;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteServiceImpl implements VoteService {

    private final UserRepository userRepository;
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

    private Vote findVote(Long id) {
        return voteRepository.findById(id)
            .orElseThrow(() -> new VoteNotFoundException(NOT_FOUND_VOTE_EXCEPTION));
    }
}
