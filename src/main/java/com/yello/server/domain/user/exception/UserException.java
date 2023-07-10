package com.yello.server.domain.user.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class UserException extends CustomException {

    public UserException(ErrorCode error, String message) {
        super(error, "[UserException] " + message);
    }
}
