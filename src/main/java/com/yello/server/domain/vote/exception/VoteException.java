package com.yello.server.domain.vote.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class VoteException extends CustomException {

    public VoteException(ErrorCode error, String message) {
        super(error, "[VoteException] " + message);
    }
}
