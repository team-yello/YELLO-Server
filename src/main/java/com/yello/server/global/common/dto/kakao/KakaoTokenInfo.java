package com.yello.server.global.common.dto.kakao;

public record KakaoTokenInfo(
        Long id,
        Integer expires_in,
        Integer app_in
) {
}
