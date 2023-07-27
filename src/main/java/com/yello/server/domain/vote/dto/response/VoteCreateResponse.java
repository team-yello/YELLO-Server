package com.yello.server.domain.vote.dto.response;

import lombok.Builder;

@Builder
public record VoteCreateResponse(
        Integer point
) {

    public static VoteCreateResponse of(Integer point) {
        return VoteCreateResponse.builder()
                .point(point)
                .build();
    }
}
