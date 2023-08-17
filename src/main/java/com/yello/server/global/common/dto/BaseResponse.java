package com.yello.server.global.common.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.common.SuccessCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseResponse<T> {

    private final int status;
    private final String message;
    @JsonInclude(NON_NULL)
    private T data;

    private BaseResponse() {
        throw new IllegalStateException();
    }

    public static <T> BaseResponse<T> success(SuccessCode success) {
        return new BaseResponse<>(success.getHttpStatusCode(), success.getMessage());
    }

    public static <T> BaseResponse<T> success(SuccessCode success, T data) {
        return new BaseResponse<>(success.getHttpStatusCode(), success.getMessage(), data);
    }

    public static <T> BaseResponse<T> error(ErrorCode error) {
        return new BaseResponse<>(error.getHttpStatusCode(), error.getMessage());
    }

    public static <T> BaseResponse<T> error(ErrorCode error, @Nullable String message) {
        return new BaseResponse<>(error.getHttpStatusCode(), message);
    }

    public static <T> BaseResponse<T> error(ErrorCode error, @Nullable String message,
        @Nullable T data) {
        return new BaseResponse<>(error.getHttpStatusCode(), message, data);
    }
} 