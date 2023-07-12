package com.yello.server.domain.authorization.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class OAuthException extends CustomException {

    public OAuthException(ErrorCode error) {
        super(error, "[OAuthException] " + error.getMessage());
    }
}
