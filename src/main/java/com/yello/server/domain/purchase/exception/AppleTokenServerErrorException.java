package com.yello.server.domain.purchase.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class AppleTokenServerErrorException extends CustomException {

    public AppleTokenServerErrorException(ErrorCode error) {
        super(error, "[AppleTokenServerErrorException] " + error.getMessage());
    }
}
