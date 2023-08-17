package com.yello.server.domain.purchase.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class SubscriptionConflictException extends CustomException {

    public SubscriptionConflictException(ErrorCode error) {
        super(error, "[SubscriptionConflictException] " + error.getMessage());
    }

}
