package com.yello.server.exception;

import com.yello.server.common.ErrorCode;

public class NotFoundException extends CustomException {
    public NotFoundException(ErrorCode error, String message) {
        super(error, message);
    }
}
