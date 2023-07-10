package com.yello.server.domain.auth.dto.response;

import com.yello.server.domain.auth.dto.ServiceTokenVO;
import lombok.Builder;

@Builder
public record OAuthResponse(
        String accessToken,
        String refreshToken
) {
    public static OAuthResponse of(ServiceTokenVO tokens){
        return OAuthResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .build();
    }
}
