package com.yello.server.domain.auth.dto.request;

import lombok.Builder;

@Builder
public record OAuthRequest(
        String accessToken,
        String social
) { }
