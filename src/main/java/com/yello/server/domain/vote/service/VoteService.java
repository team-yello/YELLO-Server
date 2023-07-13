package com.yello.server.domain.vote.service;

import com.yello.server.domain.question.dto.response.VoteAvailableResponse;
import com.yello.server.domain.question.dto.response.VoteQuestionResponse;
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

    List<VoteQuestionResponse> findYelloVoteList(Long userId);

    VoteAvailableResponse checkVoteAvailable(Long userId);

}
