package com.yello.server.domain.authorization.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;

public class AuthNotFoundException extends CustomException {
    public AuthNotFoundException(ErrorCode error) {
        super(error, "[AuthNotFoundException] " + error.getMessage());
    }
}
