package com.yello.server.domain.event.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;

public class EventNotFoundException extends CustomException {

    public EventNotFoundException(ErrorCode error) {
        super(error, "[EventNotFoundException] " + error.getMessage());
    }
}
