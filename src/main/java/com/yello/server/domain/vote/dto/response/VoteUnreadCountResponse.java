package com.yello.server.domain.vote.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record VoteUnreadCountResponse(
    @Schema(description = "읽지 않은 쪽지 개수", example = "1")
    Integer totalCount
) {

    public static VoteUnreadCountResponse of(Integer totalCount) {
        return VoteUnreadCountResponse.builder()
            .totalCount(totalCount)
            .build();
    }
}
