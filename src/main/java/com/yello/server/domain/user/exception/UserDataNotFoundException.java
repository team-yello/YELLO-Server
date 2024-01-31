package com.yello.server.domain.user.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class UserDataNotFoundException extends CustomException {

    public UserDataNotFoundException(ErrorCode error) {
        super(error, "[UserDataNotFoundException] " + error.getMessage());
    }
}
