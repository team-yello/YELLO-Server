package com.yello.server.domain.user.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class UserNotFoundException extends CustomException {

    public UserNotFoundException(ErrorCode error) {
        super(error, "[UserNotFoundException] " + error.getMessage());
    }
}
