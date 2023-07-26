package com.yello.server.domain.vote.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record VoteListResponse(
        Integer totalCount,
        List<VoteResponse> votes
) {

    public static VoteListResponse of(Integer totalCount, List<VoteResponse> votes) {
        return VoteListResponse.builder()
                .totalCount(totalCount)
                .votes(votes)
                .build();
    }
}
