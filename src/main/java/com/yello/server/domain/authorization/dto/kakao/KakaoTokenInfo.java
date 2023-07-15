package com.yello.server.domain.authorization.dto.kakao;

public record KakaoTokenInfo(
    Long id,
    Integer expires_in
) {

}
