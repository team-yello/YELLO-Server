package com.yello.server.domain.vote.service;

import com.yello.server.domain.question.dto.response.VoteAvailableResponse;
import com.yello.server.domain.question.dto.response.VoteQuestionResponse;
import com.yello.server.domain.vote.dto.request.CreateVoteRequest;
import com.yello.server.domain.vote.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VoteService {

    List<VoteResponse> findAllVotes(Long userId, Pageable pageable);

    VoteDetailResponse findVoteById(Long id);

    List<VoteFriendResponse> findAllFriendVotes(Long userId, Pageable pageable);

    KeywordCheckResponse checkKeyword(Long userId, Long voteId);

    List<VoteQuestionResponse> findYelloVoteList(Long userId);

    VoteAvailableResponse checkVoteAvailable(Long userId);

    void createVote(Long userId, CreateVoteRequest request);

    RevealNameResponse revealNameHint(Long userId, Long voteId);

}
