package com.yello.server.domain.admin.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;

public class AdminConfigurationNotFoundException extends CustomException {

    public AdminConfigurationNotFoundException(ErrorCode error) {
        super(error, "[AdminConfigurationNotFoundException] " + error.getMessage());
    }
}
