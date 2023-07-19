package com.yello.server.domain.authorization.dto;

import lombok.Builder;
import lombok.Setter;

@Builder
public record ServiceTokenVO(
    String accessToken,
    String refreshToken
) {

    public static ServiceTokenVO of(String accessToken, String refreshToken) {
        return ServiceTokenVO.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}
