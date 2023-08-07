package com.yello.server.infrastructure.redis.repository;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;

public interface TokenRepository {

    void set(Long key, ServiceTokenVO value);

    ServiceTokenVO get(Long key);

}
