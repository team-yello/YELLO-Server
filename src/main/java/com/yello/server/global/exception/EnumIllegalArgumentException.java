package com.yello.server.global.exception;

import com.yello.server.global.common.ErrorCode;
import lombok.Getter;

@Getter
public class EnumIllegalArgumentException extends CustomException {

    public EnumIllegalArgumentException(ErrorCode error) {
        super(error, "[EnumIllegalArgumentException] " + error.getMessage());
    }
}
