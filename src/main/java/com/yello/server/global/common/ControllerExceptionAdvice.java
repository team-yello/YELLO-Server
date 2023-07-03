package com.yello.server.global.common;

import com.yello.server.domain.vote.exception.VoteException;
import com.yello.server.global.common.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(VoteException.class)
    public ResponseEntity<BaseResponse> VoteException(VoteException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(BaseResponse.error(e.getError(), e.getMessage()));
    }

}
