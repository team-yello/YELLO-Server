package com.yello.server.domain.purchase.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class AppleBadRequestException extends CustomException {

    public AppleBadRequestException(ErrorCode error) {
        super(error, "[AppleBadRequestException] " + error.getMessage());
    }
}
