package com.yello.server.exception;

import com.yello.server.common.ErrorCode;

public class BadRequestException extends CustomException {
    public BadRequestException(ErrorCode error, String message) {
        super(error, message);
    }
}
