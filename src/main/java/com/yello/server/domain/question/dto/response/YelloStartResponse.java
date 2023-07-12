package com.yello.server.domain.question.dto.response;

import lombok.Builder;

@Builder
public record YelloStartResponse(
        Boolean isStart,
        Integer point,
        String createdAt
) {

}
