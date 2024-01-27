package com.yello.server.domain.notice.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class NoticeNotFoundException extends CustomException {

    public NoticeNotFoundException(ErrorCode error) {
        super(error, "[NoticeNotFoundException] " + error.getMessage());
    }
}
