package com.yello.server.domain.authorization.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class NotSignedInException extends CustomException {

    public NotSignedInException(ErrorCode error) {
        super(error, "[NotSignedInException] " + error.getMessage());
    }
}
