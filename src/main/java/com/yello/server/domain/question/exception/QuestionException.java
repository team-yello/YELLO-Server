package com.yello.server.domain.question.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class QuestionException extends CustomException {

    public QuestionException(ErrorCode error) {
        super(error, "[QuestionException] " + error.getMessage());
    }
}
