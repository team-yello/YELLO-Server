package com.yello.server.domain.authorization.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class CustomAuthenticationException extends CustomException {

    public CustomAuthenticationException(ErrorCode error) {
        super(error, "[CustomAuthenticationException] " + error.getMessage());
    }
}
