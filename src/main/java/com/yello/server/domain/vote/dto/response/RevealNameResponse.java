package com.yello.server.domain.vote.dto.response;

import lombok.Builder;

@Builder
public record RevealNameResponse(
    char name,
    Integer nameIndex
) {
}
