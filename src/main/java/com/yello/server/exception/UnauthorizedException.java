package com.yello.server.exception;


import com.yello.server.common.ErrorCode;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(ErrorCode error, String message) {
        super(error, message);
    }
}
