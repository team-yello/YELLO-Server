package com.yello.server.exception.model;

import com.yello.server.exception.ErrorCode;

public class NotFoundException extends CustomException {
    public NotFoundException(ErrorCode error, String message) {
        super(error, message);
    }
}
