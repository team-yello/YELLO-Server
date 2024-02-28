package com.yello.server.domain.event.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;

public class EventBadRequestException extends CustomException {

    public EventBadRequestException(ErrorCode error) {
        super(error, "[EventBadRequestException] " + error.getMessage());
    }
}
