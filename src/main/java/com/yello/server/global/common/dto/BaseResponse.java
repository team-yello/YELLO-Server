package com.yello.server.global.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.common.SuccessCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseResponse<T> {

    private final int status;
    private final String message;
    @JsonInclude(NON_NULL)
    private T data;

    public static BaseResponse success(SuccessCode success) {
        return new BaseResponse<>(success.getHttpStatusCode(), success.getMessage());
    }

    public static <T> BaseResponse<T> success(SuccessCode success, T data) {
        return new BaseResponse<T>(success.getHttpStatusCode(), success.getMessage(), data);
    }

    public static BaseResponse error(ErrorCode error) {
        return new BaseResponse<>(error.getHttpStatusCode(), error.getMessage());
    }

    public static BaseResponse error(ErrorCode error, @Nullable String message) {
        return new BaseResponse<>(error.getHttpStatusCode(), message);
    }

    public static <T> BaseResponse<T> error(ErrorCode error, @Nullable String message, @Nullable T data) {
        return new BaseResponse<>(error.getHttpStatusCode(), message, data);
    }
} 