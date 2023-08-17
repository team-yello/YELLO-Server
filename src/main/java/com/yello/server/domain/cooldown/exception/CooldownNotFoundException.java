package com.yello.server.domain.cooldown.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;

public class CooldownNotFoundException extends CustomException {

    public CooldownNotFoundException(ErrorCode error) {
        super(error, "[CooldownNotFoundException] " + error.getMessage());
    }
}
