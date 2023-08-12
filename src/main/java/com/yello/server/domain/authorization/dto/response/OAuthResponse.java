package com.yello.server.domain.authorization.dto.response;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import lombok.Builder;

@Builder
public record OAuthResponse(
    Boolean isResigned,
    String accessToken,
    String refreshToken
) {

    public static OAuthResponse of(Boolean isResigned, ServiceTokenVO tokens) {
        return OAuthResponse.builder()
            .isResigned(isResigned)
            .accessToken(tokens.accessToken())
            .refreshToken(tokens.refreshToken())
            .build();
    }
}
