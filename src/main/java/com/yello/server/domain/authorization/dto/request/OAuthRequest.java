package com.yello.server.domain.authorization.dto.request;

import lombok.Builder;

@Builder
public record OAuthRequest(
    String accessToken,
    String social
) {

}
