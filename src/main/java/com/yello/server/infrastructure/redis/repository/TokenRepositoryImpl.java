package com.yello.server.infrastructure.redis.repository;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {

    private final ValueOperations<Long, ServiceTokenVO> redisTokenRepository;

    @Override
    public void set(Long key, ServiceTokenVO value) {
        redisTokenRepository.set(key, value);
    }

    @Override
    public ServiceTokenVO get(Long key) {
        return redisTokenRepository.get(key);
    }
}
