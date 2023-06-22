package com.yello.server.exception.model;

import com.yello.server.exception.ErrorCode;

public class BadRequestException extends CustomException {
    public BadRequestException(ErrorCode error, String message) {
        super(error, message);
    }
}
