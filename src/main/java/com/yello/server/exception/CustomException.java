package com.yello.server.exception;

import com.yello.server.common.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode error;

    public CustomException(ErrorCode error, String message) {
        super(message);
        this.error = error;
    }

    public int getHttpStatus() {
        return error.getHttpStatusCode();
    }
}
