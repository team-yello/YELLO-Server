package com.yello.server.global.exception;

import com.yello.server.global.common.ErrorCode;

public class BadRequestException extends CustomException {
    public BadRequestException(ErrorCode error, String message) {
        super(error, message);
    }
}
