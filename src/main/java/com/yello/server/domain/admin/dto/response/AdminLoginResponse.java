package com.yello.server.domain.admin.dto.response;

import lombok.Builder;

@Builder
public record AdminLoginResponse(
    String accessToken
) {

    public static AdminLoginResponse of(String accessToken) {
        return AdminLoginResponse.builder()
            .accessToken(accessToken)
            .build();
    }
}
