package com.yello.server.domain.authorization.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;

public class NotValidTokenForbiddenException extends CustomException {

    public NotValidTokenForbiddenException(ErrorCode error) {
        super(error, "[NotValidTokenForbiddenException] " + error.getMessage());
    }
}
