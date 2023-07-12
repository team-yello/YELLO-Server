package com.yello.server.infrastructure.redis.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class RedisException extends CustomException {

    public RedisException(ErrorCode error) {
        super(error, "[RedisException] " + error.getMessage());
    }
}
