package com.yello.server.domain.purchase.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class GoogleBadRequestException extends CustomException {

    public GoogleBadRequestException(ErrorCode error) {
        super(error, "[GoogleBadRequestException] " + error.getMessage());
    }
}
