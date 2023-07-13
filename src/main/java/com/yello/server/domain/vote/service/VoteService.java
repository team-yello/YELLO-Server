package com.yello.server.domain.vote.service;

import com.yello.server.domain.question.dto.response.YelloStartResponse;
import com.yello.server.domain.question.dto.response.YelloVoteResponse;
import com.yello.server.domain.vote.dto.request.CreateVoteRequest;
import com.yello.server.domain.vote.dto.response.KeywordCheckResponse;
import com.yello.server.domain.vote.dto.response.VoteDetailResponse;
import com.yello.server.domain.vote.dto.response.VoteFriendResponse;
import com.yello.server.domain.vote.dto.response.VoteResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VoteService {

    List<VoteResponse> findAllVotes(Pageable pageable);

    VoteDetailResponse findVoteById(Long id);

    List<VoteFriendResponse> findAllFriendVotes(Pageable pageable);

    KeywordCheckResponse checkKeyword(Long userId, Long voteId);

    List<YelloVoteResponse> findYelloVoteList(Long userId);

    YelloStartResponse checkVoteAvailable(Long userId);

    void createVote(Long userId, CreateVoteRequest request);

}
