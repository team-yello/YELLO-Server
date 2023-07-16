package com.yello.server.global.exception;

import static com.yello.server.global.common.ErrorCode.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.yello.server.domain.authorization.exception.AuthBadRequestException;
import com.yello.server.domain.authorization.exception.CustomAuthenticationException;
import com.yello.server.domain.authorization.exception.ExpiredTokenException;
import com.yello.server.domain.authorization.exception.InvalidTokenException;
import com.yello.server.domain.authorization.exception.NotSignedInException;
import com.yello.server.domain.authorization.exception.OAuthException;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.group.exception.GroupNotFoundException;
import com.yello.server.domain.user.exception.UserBadRequestException;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.vote.exception.VoteForbiddenException;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.infrastructure.redis.exception.RedisException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.yello.server.global.common.ErrorCode.FIELD_REQUIRED_EXCEPTION;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler({
        FriendException.class,
        UserException.class,
        AuthBadRequestException.class,
        UserBadRequestException.class
    })
    public ResponseEntity<BaseResponse> BadRequestException(CustomException exception) {
        return ResponseEntity.status(BAD_REQUEST)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

    @ExceptionHandler({
            // @Valid 오류 Catch
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<BaseResponse> BadRequestException(BindException exception) {
        return ResponseEntity.status(BAD_REQUEST)
            .body(BaseResponse.error(FIELD_REQUIRED_EXCEPTION, FIELD_REQUIRED_EXCEPTION.getMessage()));
    }

    @ExceptionHandler({
            // Post인데 @RequestBody 없을 때
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<BaseResponse> BadRequestException(HttpMessageConversionException exception) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(BaseResponse.error(FIELD_REQUIRED_EXCEPTION, FIELD_REQUIRED_EXCEPTION.getMessage()));
    }

    @ExceptionHandler({
            // @RequestParam 이 없을 때
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<BaseResponse> BadRequestException(MissingServletRequestParameterException exception) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(BaseResponse.error(QUERY_STRING_REQUIRED_EXCEPTION, QUERY_STRING_REQUIRED_EXCEPTION.getMessage()));
    }

    @ExceptionHandler({
            // Post인데 @RequestBody 없을 때
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<BaseResponse> BadRequestException(HttpMessageConversionException exception) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(BaseResponse.error(FIELD_REQUIRED_EXCEPTION, FIELD_REQUIRED_EXCEPTION.getMessage()));
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            VoteNotFoundException.class,
            GroupNotFoundException.class
    })
    public ResponseEntity<BaseResponse> NotFoundException(CustomException exception) {
        return ResponseEntity.status(NOT_FOUND)
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
        return ResponseEntity.status(UNAUTHORIZED)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

    @ExceptionHandler({
        UserConflictException.class
    })
    public ResponseEntity<BaseResponse> ConflictException(CustomException exception) {
        return ResponseEntity.status(CONFLICT)
            .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

    @ExceptionHandler({
        RedisException.class,
    })
    public ResponseEntity<BaseResponse> InternalServerException(CustomException exception) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }

    @ExceptionHandler({
            VoteForbiddenException.class
    })
    public ResponseEntity<BaseResponse> ForbiddenException(CustomException exception) {
        return ResponseEntity.status(FORBIDDEN)
                .body(BaseResponse.error(exception.getError(), exception.getMessage()));
    }
}
