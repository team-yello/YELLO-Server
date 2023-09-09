package com.yello.server.domain.admin.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;

public class UserAdminBadRequestException extends CustomException {

    public UserAdminBadRequestException(ErrorCode error) {
        super(error, "[UserAdminBadRequestException] " + error.getMessage());
    }
}
