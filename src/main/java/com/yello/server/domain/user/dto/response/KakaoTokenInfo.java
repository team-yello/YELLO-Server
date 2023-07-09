package com.yello.server.domain.user.dto.response;

public record KakaoTokenInfo(
        Long id,
        Integer expires_in,
        Integer app_in
) {
}
