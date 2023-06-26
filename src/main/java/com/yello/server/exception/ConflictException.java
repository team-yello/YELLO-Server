package com.yello.server.exception;

import com.yello.server.common.ErrorCode;

public class ConflictException extends CustomException {
    public ConflictException(ErrorCode error, String message) {
        super(error, message);
    }
}
