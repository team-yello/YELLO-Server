package com.yello.server.global.exception;


import com.yello.server.global.common.ErrorCode;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(ErrorCode error, String message) {
        super(error, message);
    }
}
