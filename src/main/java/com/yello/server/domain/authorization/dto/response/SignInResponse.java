package com.yello.server.domain.authorization.dto.response;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import lombok.Builder;

@Builder
public record SignInResponse(
       String yelloId,
       String accessToken,
       String refreshToken
) {
    public static SignInResponse of (String yelloId, ServiceTokenVO tokens) {
        return SignInResponse.builder()
                .yelloId(yelloId)
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .build();
    }
}
