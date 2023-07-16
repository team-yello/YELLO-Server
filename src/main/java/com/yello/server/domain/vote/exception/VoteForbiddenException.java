package com.yello.server.domain.vote.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class VoteForbiddenException extends CustomException {

    public VoteForbiddenException(ErrorCode error) {
        super(error, "[VoteForbiddenException] " + error.getMessage());
    }
}
