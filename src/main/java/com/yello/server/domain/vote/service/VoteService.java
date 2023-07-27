package com.yello.server.domain.vote.service;

import com.yello.server.domain.keyword.dto.response.KeywordCheckResponse;
import com.yello.server.domain.question.dto.response.QuestionForVoteResponse;
import com.yello.server.domain.vote.dto.request.CreateVoteRequest;
import com.yello.server.domain.vote.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VoteService {

    VoteListResponse findAllVotes(Long userId, Pageable pageable);

    VoteDetailResponse findVoteById(Long id);

    List<VoteFriendResponse> findAllFriendVotes(Long userId, Pageable pageable);

    KeywordCheckResponse checkKeyword(Long userId, Long voteId);

    List<QuestionForVoteResponse> findVoteQuestionList(Long userId);

    VoteAvailableResponse checkVoteAvailable(Long userId);

    VoteCreateResponse createVote(Long userId, CreateVoteRequest request);

    RevealNameResponse revealNameHint(Long userId, Long voteId);

}
