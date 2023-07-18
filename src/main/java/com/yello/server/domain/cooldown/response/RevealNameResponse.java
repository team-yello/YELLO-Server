package com.yello.server.domain.cooldown.response;

import lombok.Builder;

@Builder
public record RevealNameResponse(
    char name
) {

}
