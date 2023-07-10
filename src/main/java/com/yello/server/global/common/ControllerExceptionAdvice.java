package com.yello.server.global.common;

import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import com.yello.server.global.common.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionAdvice {

    //vote
    @ExceptionHandler(VoteNotFoundException.class)
    public ResponseEntity<BaseResponse> VoteException(VoteNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

    // user
    @ExceptionHandler(UserException.class)
    public ResponseEntity<BaseResponse> UserException(UserException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<BaseResponse> UserNotFoundException(UserNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

    // friend
    @ExceptionHandler(FriendException.class)
    public ResponseEntity<BaseResponse> FriendException(FriendException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

}
