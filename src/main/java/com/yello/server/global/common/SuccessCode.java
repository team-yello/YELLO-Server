package com.yello.server.global.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {
    /**
     * 200 OK
     */
    READ_VOTE_SUCCESS(HttpStatus.OK, "투표 조회에 성공했습니다."),
    READ_FRIEND_SUCCESS(HttpStatus.OK, "친구 조회에 성공했습니다."),
    READ_USER_SUCCESS(HttpStatus.OK, "유저 조회에 성공했습니다."),
    ADD_FRIEND_SUCCESS(HttpStatus.OK, "친구 추가에 성공했습니다."),
    SHUFFLE_FRIEND_SUCCESS(HttpStatus.OK, "친구 셔플에 성공했습니다."),

    /**
     * 201 CREATED
     */
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료됐습니다."),
    CREATE_BOARD_SUCCESS(HttpStatus.CREATED, "게시물 생성이 완료됐습니다."),
    LOGIN_SUCCESS(HttpStatus.CREATED, " 로그인이 성공했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
