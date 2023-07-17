package com.yello.server.domain.vote.dto.response;

import java.util.List;
import lombok.Builder;

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
