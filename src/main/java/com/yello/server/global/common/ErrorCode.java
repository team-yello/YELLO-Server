package com.yello.server.global.common;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    /**
     * 400 BAD REQUEST
     */
    REQUEST_VALIDATION_EXCEPTION(BAD_REQUEST, "잘못된 요청입니다."),
    YELLOID_REQUIRED_EXCEPTION(BAD_REQUEST, "쿼리 스트링에 yelloId를 포함해야 합니다."),
    OAUTH_ACCESS_TOKEN_REQUIRED_EXCEPTION(BAD_REQUEST, "소셜 액세스 토큰이 없습니다."),
    LACK_USER_EXCEPTION(BAD_REQUEST, "친구가 4명 이하입니다."),
    SIGNIN_FIELD_REQUIRED_EXCEPTION(BAD_REQUEST, "회원가입에 필요한 값이 없습니다."),
    FIELD_REQUIRED_EXCEPTION(BAD_REQUEST, "필요한 값이 없습니다."),
    INVALID_VOTE_EXCEPTION(BAD_REQUEST, "이미 공개한 투표입니다"),
    QUERY_STRING_REQUIRED_EXCEPTION(BAD_REQUEST, "쿼리 스트링이 없습니다."),
    EXIST_FRIEND_EXCEPTION(BAD_REQUEST, "이미 존재하는 친구입니다."),

    /**
     * 401 UNAUTHORIZED
     */
    EXPIRED_TOKEN(UNAUTHORIZED, "만료된 토큰입니다."),
    MALFORMED_TOKEN(UNAUTHORIZED, "변조된 토큰입니다."),
    SIGNATURE_TOKEN(UNAUTHORIZED, "서명되지 않은 토큰입니다."),
    ILLEGAL_TOKEN(UNAUTHORIZED, "정상적이지 않은 토큰입니다."),
    INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EMPTY_TOKEN(UNAUTHORIZED, "비어있는 토큰입니다."),
    OAUTH_TOKEN_EXCEPTION(UNAUTHORIZED, "잘못된 소셜 토큰입니다."),
    AUTHENTICATION_ERROR(UNAUTHORIZED, "Bearer로 시작하지 않는 토큰입니다."),

    /**
     * 403 FORBIDDEN
     */
    NOT_SIGNIN_USER_EXCEPTION(FORBIDDEN, "가입하지 않은 회원입니다."),
    LACK_POINT_EXCEPTION(FORBIDDEN, "포인트가 부족합니다"),
    TOKEN_NOT_EXPIRED_AUTH_EXCEPTION(FORBIDDEN, "토큰이 모두 유효합니다."),
    TOKEN_ALL_EXPIRED_AUTH_EXCEPTION(FORBIDDEN, "토큰이 모두 만료됐습니다."),
    TOKEN_INFO_NOT_SAME_AUTH_EXCEPTION(FORBIDDEN, "동일하지 않은 액세스 토큰과 리프레시 토큰입니다."),

    /**
     * 404 NOT FOUND
     */
    YELLOID_NOT_FOUND_USER_EXCEPTION(NOT_FOUND, "탈퇴했거나 존재하지 않는 유저의 yelloId입니다."),
    AUTH_NOT_FOUND_USER_EXCEPTION(NOT_FOUND, "토큰 정보에 해당하는 유저가 존재하지 않습니다."),
    AUTH_UUID_NOT_FOUND_USER_EXCEPTION(NOT_FOUND, "토큰 uuid에 해당하는 유저가 존재하지 않습니다."),
    GROUPID_NOT_FOUND_GROUP_EXCEPTION(NOT_FOUND, "해당 그룹 id의 그룹이 존재하지 않습니다."),
    USERID_NOT_FOUND_USER_EXCEPTION(NOT_FOUND, "탈퇴했거나 존재하지 않는 유저의 id 입니다."),
    TOKEN_TIME_EXPIRED_EXCEPTION(UNAUTHORIZED, "인증되지 않은 유저입니다."),
    NOT_FOUND_VOTE_EXCEPTION(NOT_FOUND, "존재하지 않는 투표입니다."),
    NOT_FOUND_QUESTION_EXCEPTION(NOT_FOUND, "존재하지 않는 질문입니다."),
    NOT_FOUND_FRIEND_EXCEPTION(NOT_FOUND, "존재하지 않는 친구이거나 친구 관계가 아닙니다."),

    /**
     * 409 CONFLICT
     */
    ALREADY_EXIST_USER_EXCEPTION(CONFLICT, "이미 존재하는 유저입니다"),
    YELLOID_CONFLICT_USER_EXCEPTION(CONFLICT, "이미 존재하는 yelloId 입니다."),
    UUID_CONFLICT_USER_EXCEPTION(CONFLICT, "이미 존재하는 소셜 유저입니다."),

    /**
     * 500 INTERNAL SERVER ERROR
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러가 발생했습니다"),
    REDIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis에 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}