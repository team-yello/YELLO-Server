package com.yello.server.exception.model;

import com.yello.server.exception.ErrorCode;

public class ConflictException extends CustomException {
    public ConflictException(ErrorCode error, String message) {
        super(error, message);
    }
}
