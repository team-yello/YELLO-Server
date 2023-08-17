package com.yello.server.domain.purchase.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class PurchaseException extends CustomException {

    public PurchaseException(ErrorCode error) {
        super(error, "[PurchaseException] " + error.getMessage());
    }
}
