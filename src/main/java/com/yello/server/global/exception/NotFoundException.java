package com.yello.server.global.exception;

import com.yello.server.global.common.ErrorCode;

public class NotFoundException extends CustomException {
    public NotFoundException(ErrorCode error, String message) {
        super(error, message);
    }
}
