package com.yello.server.domain.vote.dto.response;

import lombok.Builder;

@Builder
public record VoteCreateResponse(
        int point
) {
}
