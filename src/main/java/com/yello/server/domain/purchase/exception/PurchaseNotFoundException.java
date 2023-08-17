package com.yello.server.domain.purchase.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class PurchaseNotFoundException extends CustomException {

    public PurchaseNotFoundException(ErrorCode error) {
        super(error, "[PurchaseNotFoundException] " + error.getMessage());
    }
}
