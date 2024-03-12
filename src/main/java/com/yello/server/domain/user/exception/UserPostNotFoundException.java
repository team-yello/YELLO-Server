package com.yello.server.domain.user.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class UserPostNotFoundException extends CustomException {

    public UserPostNotFoundException(ErrorCode error) {
        super(error, "[UserPostNotFoundException] " + error.getMessage());
    }
}
