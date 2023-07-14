package com.yello.server.domain.vote.dto.request;

import lombok.Builder;

import java.util.List;


@Builder
public record CreateVoteRequest(
        List<VoteAnswer> voteAnswerList,
        Integer totalPoint

) {
}
