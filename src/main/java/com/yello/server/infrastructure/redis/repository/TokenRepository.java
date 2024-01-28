package com.yello.server.infrastructure.redis.repository;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;

/**
 * 해당 Repository는 Redis의 io.lettuce.core.RedisReadOnlyException 오류 및 Refresh Token 구조 refactor이 진행되기 전까지 사용하지 않습니다.
 *
 * <a href="https://yelloteamworkspace.slack.com/archives/C05MRUK89R9/p1705231057902139">...</a>
 * <a href="https://hyeonic.github.io/woowacourse/dallog/google-refresh-token.html">...</a>
 */
public interface TokenRepository {

    void set(Long key, ServiceTokenVO value);

    ServiceTokenVO get(Long key);

}
