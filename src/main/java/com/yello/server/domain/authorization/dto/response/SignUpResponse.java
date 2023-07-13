package com.yello.server.domain.authorization.dto.response;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import lombok.Builder;

@Builder
public record SignUpResponse(
       String yelloId,
       String accessToken,
       String refreshToken
) {
    public static SignUpResponse of (String yelloId, ServiceTokenVO tokens) {
        return SignUpResponse.builder()
                .yelloId(yelloId)
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .build();
    }
}
