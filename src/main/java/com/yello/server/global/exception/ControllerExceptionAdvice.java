package com.yello.server.global.exception;

import com.yello.server.domain.authorization.exception.*;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.infrastructure.redis.exception.RedisException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.yello.server.global.common.ErrorCode.FIELD_REQUIRED_EXCEPTION;

@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler({
        FriendException.class,
        UserException.class,
        AuthBadRequestException.class
    })
    public ResponseEntity<BaseResponse> BadRequestException(CustomException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

//    @Valid 오류 Catch
    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<BaseResponse> BadRequestException(BindException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(FIELD_REQUIRED_EXCEPTION, FIELD_REQUIRED_EXCEPTION.getMessage()));
    }

    @ExceptionHandler({
        UserNotFoundException.class,
        VoteNotFoundException.class
    })
    public ResponseEntity<BaseResponse> NotFoundException(CustomException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

    @ExceptionHandler({
        CustomAuthenticationException.class,
        ExpiredTokenException.class,
        InvalidTokenException.class,
        NotSignedInException.class,
        OAuthException.class
    })
    public ResponseEntity<BaseResponse> UnauthorizedException(CustomException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

    @ExceptionHandler({
            UserConflictException.class
    })
    public ResponseEntity<BaseResponse> ConflictException(CustomException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

    @ExceptionHandler({
        RedisException.class,
    })
    public ResponseEntity<BaseResponse> InternalServerException(CustomException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }
}
