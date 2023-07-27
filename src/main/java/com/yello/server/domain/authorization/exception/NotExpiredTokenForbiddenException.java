package com.yello.server.domain.authorization.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class NotExpiredTokenForbiddenException extends CustomException {

    public NotExpiredTokenForbiddenException(ErrorCode error) {
        super(error, "[NotExpiredTokenException] " + error.getMessage());
    }
}
