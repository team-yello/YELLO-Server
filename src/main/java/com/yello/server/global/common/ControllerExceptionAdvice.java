package com.yello.server.global.common;

import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.domain.vote.exception.VoteException;
import com.yello.server.global.common.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionAdvice {
/*

    //vote
    @ExceptionHandler(VoteException.class)
    public ResponseEntity<BaseResponse> VoteException(VoteException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(BaseResponse.error(e.getError(), e.getMessage()));
    }

    // user
    @ExceptionHandler(UserException.class)
    public ResponseEntity<BaseResponse> UserException(UserException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error(e.getError(), e.getMessage()));
    }

    // friend
    @ExceptionHandler(FriendException.class)
    public ResponseEntity<BaseResponse> FriendException(FriendException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getError(), e.getMessage()));
    }
*/

}
