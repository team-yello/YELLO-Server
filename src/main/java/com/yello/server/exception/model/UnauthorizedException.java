package com.yello.server.exception.model;


import com.yello.server.exception.ErrorCode;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(ErrorCode error, String message) {
        super(error, message);
    }
}
