package com.yello.server.domain.user.dto.response;

import lombok.Builder;

@Builder
public record ServiceTokenVO(
        String accessToken,
        String refreshToken
) { }