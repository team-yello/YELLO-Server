package com.yello.server.infrastructure.redis.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class RedisNotFoundException extends CustomException {

    public RedisNotFoundException(ErrorCode error) {
        super(error, "[RedisNotFoundException] " + error.getMessage());
    }
}
