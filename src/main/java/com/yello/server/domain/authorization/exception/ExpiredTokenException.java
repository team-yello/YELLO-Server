package com.yello.server.domain.authorization.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class ExpiredTokenException extends CustomException {

    public ExpiredTokenException(ErrorCode error) {
        super(error, "[ExpiredTokenException] " + error.getMessage());
    }
}
