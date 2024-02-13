package com.yello.server.domain.event.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;

public class EventForbiddenException extends CustomException {

    public EventForbiddenException(ErrorCode error) {
        super(error, "[EventForbiddenException] " + error.getMessage());
    }
}
