package com.yello.server.domain.purchase.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class GoogleTokenServerErrorException extends CustomException {

    public GoogleTokenServerErrorException(ErrorCode error) {
        super(error, "[GoogleTokenServerErrorException] " + error.getMessage());
    }
}
