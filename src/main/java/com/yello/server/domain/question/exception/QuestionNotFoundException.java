package com.yello.server.domain.question.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class QuestionNotFoundException extends CustomException {

    public QuestionNotFoundException(ErrorCode error) {
        super(error, "[QuestionNotFoundException] " + error.getMessage());
    }
}
