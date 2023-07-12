package com.yello.server.domain.authorization.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class InvalidTokenException extends CustomException {

    public InvalidTokenException(ErrorCode error) {
        super(error, "[InvalidTokenException] " + error.getMessage());
    }
}
