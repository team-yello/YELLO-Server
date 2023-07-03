package com.yello.server.domain.vote.service;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_VOTE_EXCEPTION;

import com.yello.server.domain.vote.dto.response.VoteDetailResponse;
import com.yello.server.domain.vote.dto.response.VoteFriendResponse;
import com.yello.server.domain.vote.dto.response.VoteResponse;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.entity.VoteRepository;
import com.yello.server.domain.vote.exception.VoteException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    //    private final UserRepository userRepository;
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

    private Vote findVote(Long id) {
        return voteRepository.findById(id)
            .orElseThrow(() -> new VoteException(
                NOT_FOUND_VOTE_EXCEPTION,
                NOT_FOUND_VOTE_EXCEPTION.getMessage())
            );
    }
}
