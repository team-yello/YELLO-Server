package com.yello.server.domain.authorization.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;

public class AuthBadRequestException extends CustomException {

    public AuthBadRequestException(ErrorCode error) {
        super(error, "[AuthBadRequestException] " + error.getMessage());
    }
}
