package com.yello.server.global.common.advice;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.global.exception.CustomException;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionAdvice {

    /**
     * 400 BAD_REQUEST
     */

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected BaseResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        FieldError fieldError = Objects.requireNonNull(e.getFieldError());
        return BaseResponse.error(ErrorCode.REQUEST_VALIDATION_EXCEPTION,
            String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField()));
    }

/*

    /**
     * 500 Internal Server
     */

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected BaseResponse<Object> handleException(final Exception e) {
        return BaseResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * Sopt custom error - soptExcpetion 받아서 에러처리해줌 (어떤 에러때문인지 알 수 있음)
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<BaseResponse> handleSoptException(CustomException e) {
        return ResponseEntity.status(e.getHttpStatus())
            .body(BaseResponse.error(e.getError(), e.getMessage()));
    }
}
