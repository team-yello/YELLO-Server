package com.yello.server.global.common;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    /**
     * 400 BAD REQUEST
     */
    REQUEST_VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    KAKAO_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 카카오 토큰입니다."),
    NO_REQUEST_DATA_EXCEPTION(HttpStatus.BAD_REQUEST, "필요한 값이 없습니다."),

    /**
     * 403 FORBIDDEN
     */
    //auth
    TOKEN_EXPIRED_EXCEPTION(HttpStatus.FORBIDDEN, "토큰이 만료되었습니다."),
    TOKEN_MALFORMED_EXCEPTION(HttpStatus.FORBIDDEN, "토큰이 변조되었습니다."),
    TOKEN_SIGNATURE_EXCEPTION(HttpStatus.FORBIDDEN, "토큰이 잘못되었습니다."),
    NOT_SIGNIN_USER_EXCEPTION(HttpStatus.FORBIDDEN, "가입하지 않은 회원입니다."),

    //friend
    FRIEND_COUNT_LACK_EXCEPTION(HttpStatus.BAD_REQUEST, "친구가 4명 이하입니다."),
    EXIST_FRIEND_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 존재하는 친구입니다."),

    /**
     * 403 Unauthorized
     */
    INVALID_PASSWORD_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효하지 않은 비밀번호입니다."),

    /**
     * 404 NOT FOUND
     */
    NOT_FOUND_USER_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),

    NOT_FOUND_SAVE_IMAGE_EXCEPTION(HttpStatus.NOT_FOUND, "이미지가 저장되지 않았습니다."),
    NOT_FOUND_IMAGE_EXCEPTION(HttpStatus.NOT_FOUND, "이미지가 없습니다."),
    NOT_FOUND_VOTE_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 투표입니다."),

    /**
     * 409 CONFLICT
     */
    ALREADY_EXIST_USER_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 유저입니다."),

    /**
     * 500 INTERNAL SERVER ERROR
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}