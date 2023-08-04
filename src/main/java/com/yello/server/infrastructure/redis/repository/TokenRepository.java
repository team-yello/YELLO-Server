package com.yello.server.infrastructure.redis.repository;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;

public interface TokenRepository {

    void set(Long key, ServiceTokenVO value);

    void setDeviceToken(String uuid, String deviceToken);

    ServiceTokenVO get(Long key);

    String getDeviceToken(String uuid);

    void deleteDeviceToken(String uuid);

    Boolean hasKey(String uuid);

}
