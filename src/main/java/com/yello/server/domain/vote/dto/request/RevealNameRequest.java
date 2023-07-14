package com.yello.server.domain.vote.dto.request;

import lombok.Builder;

@Builder
public record RevealNameRequest(
        char name
) {
}
