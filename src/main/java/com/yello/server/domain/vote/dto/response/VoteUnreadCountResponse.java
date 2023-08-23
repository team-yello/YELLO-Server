package com.yello.server.domain.vote.dto.response;

import lombok.Builder;

@Builder
public record VoteUnreadCountResponse(
    Integer totalCount
) {

    public static VoteUnreadCountResponse of(Integer totalCount) {
        return VoteUnreadCountResponse.builder()
            .totalCount(totalCount)
            .build();
    }
}
