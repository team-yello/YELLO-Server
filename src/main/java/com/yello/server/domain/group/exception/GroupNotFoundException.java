package com.yello.server.domain.group.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class GroupNotFoundException extends CustomException {

    public GroupNotFoundException(ErrorCode error) {
        super(error, "[GroupNotFoundException] " + error.getMessage());
    }
}
