package com.yello.server.domain.user.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class UserConflictException extends CustomException {

    public UserConflictException(ErrorCode error) {
        super(error, "[UserConflictException] " + error.getMessage());
    }
}
