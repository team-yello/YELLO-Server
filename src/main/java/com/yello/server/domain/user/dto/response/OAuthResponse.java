package com.yello.server.domain.user.dto.response;

import com.yello.server.domain.vote.dto.response.VoteResponse;
import com.yello.server.domain.vote.entity.Vote;
import lombok.Builder;

import static com.yello.server.global.common.util.TimeUtil.toFormattedString;

@Builder
public record OAuthResponse(
    boolean isSigned,
    Token token
) {
    @Builder
    public record Token(
            String accessToken,
            String refreshToken
    ) { }

    public static OAuthResponse of(boolean isSigned, String accessToken, String refreshToken) {
        return OAuthResponse.builder()
                .isSigned(isSigned)
                .token(Token.builder().accessToken(accessToken).refreshToken(refreshToken).build())
                .build();
    }
}
