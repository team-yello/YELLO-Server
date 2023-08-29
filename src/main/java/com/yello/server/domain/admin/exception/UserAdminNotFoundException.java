package com.yello.server.domain.admin.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;

public class UserAdminNotFoundException extends CustomException {

    public UserAdminNotFoundException(ErrorCode error) {
        super(error, "[UserAdminNotFoundException] " + error.getMessage());
    }
}
