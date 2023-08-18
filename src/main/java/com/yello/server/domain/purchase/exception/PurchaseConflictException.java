package com.yello.server.domain.purchase.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class PurchaseConflictException extends CustomException {

    public PurchaseConflictException(ErrorCode error) {
        super(error, "[PurchaseConflictException] " + error.getMessage());
    }
}
