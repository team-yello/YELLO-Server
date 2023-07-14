package com.yello.server.domain.question.dto.response;

import lombok.Builder;

@Builder
public record VoteAvailableResponse(
        Boolean isStart,
        Integer point,
        String createdAt
) {

}
