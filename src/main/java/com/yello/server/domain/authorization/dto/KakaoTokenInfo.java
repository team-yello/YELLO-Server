package com.yello.server.domain.authorization.dto;

public record KakaoTokenInfo(
    Long id,
    Integer expires_in
) {

}
