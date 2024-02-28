package com.yello.server.domain.authorization.service;

import java.time.Duration;

public interface TokenProvider {

    Long getUserId(String token);

    String getUserUuid(String token);

    boolean isExpired(String token);

    String createAccessToken(Long userId, String uuid, Duration duration);

    String createRefreshToken(Long userId, String uuid, Duration duration);

}
