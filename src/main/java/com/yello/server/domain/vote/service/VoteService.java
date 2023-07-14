package com.yello.server.domain.vote.service;

import com.yello.server.domain.question.dto.response.VoteAvailableResponse;
import com.yello.server.domain.question.dto.response.VoteQuestionResponse;
import com.yello.server.domain.vote.dto.request.CreateVoteRequest;
import com.yello.server.domain.vote.dto.response.KeywordCheckResponse;
import com.yello.server.domain.vote.dto.response.VoteDetailResponse;
import com.yello.server.domain.vote.dto.response.VoteFriendResponse;
import com.yello.server.domain.vote.dto.response.VoteResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface VoteService {

    List<VoteResponse> findAllVotes(Long userId, Pageable pageable);

    VoteDetailResponse findVoteById(Long id);

    List<VoteFriendResponse> findAllFriendVotes(Long userId, Pageable pageable);

    KeywordCheckResponse checkKeyword(Long userId, Long voteId);

    List<VoteQuestionResponse> findYelloVoteList(Long userId);

    VoteAvailableResponse checkVoteAvailable(Long userId);

    void createVote(Long userId, CreateVoteRequest request);

}
