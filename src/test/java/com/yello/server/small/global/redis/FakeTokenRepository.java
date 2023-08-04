package com.yello.server.small.global.redis;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.infrastructure.redis.repository.TokenRepository;
import java.util.HashMap;

public class FakeTokenRepository implements TokenRepository {

    private final HashMap<Long, ServiceTokenVO> data = new HashMap<>();

    @Override
    public void set(Long key, ServiceTokenVO value) {
        data.put(key, value);
    }

    @Override
    public ServiceTokenVO get(Long key) {
        return data.get(Long.parseLong(key.toString()));
    }
}
