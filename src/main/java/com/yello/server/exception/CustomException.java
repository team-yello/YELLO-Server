package com.yello.server.exception.model;

import com.yello.server.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ErrorCode error;

    public CustomException(ErrorCode error, String message) {
        super(message);
        this.error = error;
    }

    public int getHttpStatus() {
        return error.getHttpStatusCode();
    }
}
