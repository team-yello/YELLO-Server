package com.yello.server.global.exception;

import com.yello.server.global.common.ErrorCode;

public class ConflictException extends CustomException {
    public ConflictException(ErrorCode error, String message) {
        super(error, message);
    }
}
