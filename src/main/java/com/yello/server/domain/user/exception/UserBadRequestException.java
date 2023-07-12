package com.yello.server.domain.user.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class UserBadRequestException extends CustomException {

    public UserBadRequestException(ErrorCode error) {
        super(error, "[UserBadRequestException] " + error.getMessage());
    }
}
