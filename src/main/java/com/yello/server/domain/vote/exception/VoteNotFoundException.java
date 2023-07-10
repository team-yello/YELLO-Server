package com.yello.server.domain.vote.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class VoteNotFoundException extends CustomException {

    public VoteNotFoundException(ErrorCode error) {
        super(error, "[VoteNotFoundException] " + error.getMessage());
    }
}
