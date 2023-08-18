package com.yello.server.domain.purchase.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class GoogleTokenNotFoundException extends CustomException {

    public GoogleTokenNotFoundException(ErrorCode error) {
        super(error, "[GoogleTokenNotFoundException] " + error.getMessage());
    }
}
