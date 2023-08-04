package com.yello.server.infrastructure.redis.repository;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {

    private final ValueOperations<Long, ServiceTokenVO> redisTokenRepository;
    private final StringRedisTemplate redisDeviceTokenRepository;

    @Override
    public void set(Long key, ServiceTokenVO value) {
        redisTokenRepository.set(key, value);
    }

    @Override
    public void setDeviceToken(String uuid, String deviceToken) {
        redisDeviceTokenRepository.opsForValue()
            .set(uuid, deviceToken);
    }

    @Override
    public ServiceTokenVO get(Long key) {
        return redisTokenRepository.get(key);
    }

    @Override
    public String getDeviceToken(String uuid) {
        return redisDeviceTokenRepository.opsForValue()
            .get(uuid);
    }

    @Override
    public void deleteDeviceToken(String uuid) {
        redisDeviceTokenRepository.delete(uuid);
    }

    @Override
    public Boolean hasKey(String uuid) {
        return redisDeviceTokenRepository.hasKey(uuid);
    }
}
