package com.yello.server.domain.authorization.service;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;

public interface TokenProvider {

    Long getUserId(String token);

    String getUserUuid(String token);

    boolean isExpired(String token);

    String createAccessToken(Long userId, String uuid);

    String createRefreshToken(Long userId, String uuid);

    ServiceTokenVO createServiceToken(Long userId, String uuid);

    String createJwt(Long userId, String uuid, Long tokenValidTime, String tokenType);
}
