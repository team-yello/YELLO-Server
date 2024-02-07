package com.yello.server.global.common;


import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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
    REQUEST_VALIDATION_EXCEPTION(BAD_REQUEST, "잘못된 요청입니다."),
    YELLOID_REQUIRED_EXCEPTION(BAD_REQUEST, "쿼리 스트링에 yelloId를 포함해야 합니다."),
    OAUTH_ACCESS_TOKEN_REQUIRED_EXCEPTION(BAD_REQUEST, "소셜 액세스 토큰이 없습니다."),
    LACK_USER_EXCEPTION(BAD_REQUEST, "친구가 부족합니다."),
    SIGNIN_FIELD_REQUIRED_EXCEPTION(BAD_REQUEST, "회원가입에 필요한 값이 없습니다."),
    FIELD_REQUIRED_EXCEPTION(BAD_REQUEST, "필요한 값이 없습니다."),
    INVALID_VOTE_EXCEPTION(BAD_REQUEST, "이미 공개한 투표입니다"),
    QUERY_STRING_REQUIRED_EXCEPTION(BAD_REQUEST, "쿼리 스트링이 없습니다."),
    EXIST_FRIEND_EXCEPTION(BAD_REQUEST, "이미 존재하는 친구입니다."),
    REVEAL_FULL_NAME_VOTE_EXCEPTION(BAD_REQUEST, "이미 공개한 이름입니다"),
    GOOGLE_SUBSCRIPTION_USED_EXCEPTION(BAD_REQUEST, "이미 처리된 Google 영수증입니다."),
    GOOGLE_SUBSCRIPTION_DUPLICATED_CANCEL_EXCEPTION(BAD_REQUEST,
        "이미 CANCELED한 유저에게 CANCELED 요청을 보내어 중복됩니다"),
    GOOGLE_INAPP_BAD_REQUEST_EXCEPTION(BAD_REQUEST, "해당 영수증은 취소되었거나, 대기 중인 결제입니다."),
    GOOGLE_NOTIFICATION_BAD_REQUEST_EXCEPTION(BAD_REQUEST, "올바르지 않은 Pub-Sub의 요청입니다."),
    APPLE_IN_APP_BAD_REQUEST_EXCEPTION(BAD_REQUEST, "존재하지 않는 영수증입니다."),
    METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION(BAD_REQUEST, "입력한 값의 타입이 올바르지 않습니다."),
    USER_ADMIN_BAD_REQUEST_EXCEPTION(BAD_REQUEST, "검색 필드가 없습니다."),
    USER_DATA_INVALID_ARGUMENT_EXCEPTION(BAD_REQUEST, "입력한 유저 데이터의 값이 올바르지 않습니다."),
    ENUM_BAD_REQUEST_EXCEPTION(BAD_REQUEST, "존재하지 않는 열거형 타입입니다."),
    PROBABILITY_BAD_REQUEST_EXCEPTION(BAD_REQUEST, "확률의 합이 100이 아닙니다."),
    IDEMPOTENCY_KEY_BAD_REQUEST_EXCEPTION(BAD_REQUEST, "멱등성 키를 헤더에 명시되어 있지 않습니다."),
    IDEMPOTENCY_KEY_INVALID_FORM_BAD_REQUEST_EXCEPTION(BAD_REQUEST, "멱등성 키가 유효한 uuid4 형식이 아닙니다."),
    EVENT_DATE_BAD_REQUEST_EXCEPTION(BAD_REQUEST, "해당 이벤트는 현재 유효한 날짜가 아닙니다."),
    EVENT_TIME_BAD_REQUEST_EXCEPTION(BAD_REQUEST, "해당 이벤트는 현재 유효한 시간이 아닙니다."),
    EVENT_COUNT_BAD_REQUEST_EXCEPTION(BAD_REQUEST, "해당 이벤트는 보상 횟수가 전부 소진되었습니다."),

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
    DUPLICATE_VOTE_EXCEPTION(FORBIDDEN, "중복된 투표가 전달되었습니다."),
    LACK_TICKET_COUNT_EXCEPTION(FORBIDDEN, "열람권 수가 부족합니다."),
    GOOGLE_TOKEN_FORBIDDEN_EXCEPTION(FORBIDDEN,
        "유효하지 않는 Google OAuth 2.0 refreshToken입니다. DBA에게 문의해주세요."),
    GOOGLE_SUBSCRIPTIONS_FORBIDDEN_EXCEPTION(FORBIDDEN, "이미 YELLO: PLUS를 구독한 상태입니다."),
    GOOGLE_SUBSCRIPTION_TRANSACTION_EXPIRED_EXCEPTION(FORBIDDEN, "이미 만료된 결제 내역의 영수증입니다."),
    WRONG_VOTE_TYPE_FORBIDDEN(FORBIDDEN, "잘못된 투표 유형입니다."),

    /**
     * 404 NOT FOUND
     */
    YELLOID_NOT_FOUND_USER_EXCEPTION(NOT_FOUND, "탈퇴했거나 존재하지 않는 유저의 yelloId입니다."),
    DEVICE_TOKEN_NOT_FOUND_USER_EXCEPTION(NOT_FOUND, "해당 Device Token의 유저가 존재하지 않습니다."),
    AUTH_NOT_FOUND_USER_EXCEPTION(NOT_FOUND, "토큰 정보에 해당하는 유저가 존재하지 않습니다."),
    AUTH_UUID_NOT_FOUND_USER_EXCEPTION(NOT_FOUND, "토큰 uuid에 해당하는 유저가 존재하지 않습니다."),
    GROUPID_NOT_FOUND_GROUP_EXCEPTION(NOT_FOUND, "해당 그룹 id의 그룹이 존재하지 않습니다."),
    ID_NOT_FOUND_COOLDOWN_EXCEPTION(NOT_FOUND, "해당 id의 cooldown이 존재하지 않습니다."),
    USERID_NOT_FOUND_USER_EXCEPTION(NOT_FOUND, "탈퇴했거나 존재하지 않는 유저의 id 입니다."),
    TOKEN_TIME_EXPIRED_EXCEPTION(UNAUTHORIZED, "인증되지 않은 유저입니다."),
    NOT_FOUND_VOTE_EXCEPTION(NOT_FOUND, "존재하지 않는 투표입니다."),
    NOT_FOUND_QUESTION_EXCEPTION(NOT_FOUND, "존재하지 않는 질문입니다."),
    NOT_FOUND_FRIEND_EXCEPTION(NOT_FOUND, "존재하지 않는 친구이거나 친구 관계가 아닙니다."),
    REDIS_NOT_FOUND_UUID(NOT_FOUND, "uuid에 해당하는 디바이스 토큰 정보를 찾을 수 없습니다."),
    NOT_FOUND_TRANSACTION_EXCEPTION(NOT_FOUND, "존재하지 않는 거래입니다"),
    PURCHASE_TOKEN_NOT_FOUND_PURCHASE_EXCEPTION(NOT_FOUND, "purchaseToken에 해당하는 Purchase가 없습니다"),
    NOT_FOUND_PRODUCT_ID_EXCEPTION(NOT_FOUND, "존재하지 않는 상품 아이디 입니다"),
    GOOGLE_TOKEN_NOT_FOUND_EXCEPTION(NOT_FOUND, "Google OAuth 2.0 토큰 튜플이 DB에 없습니다. DBA에게 문의해주세요."),
    GOOGLE_TOKEN_FIELD_NOT_FOUND_EXCEPTION(NOT_FOUND,
        "Google OAuth 2.0 특정 토큰이 DB에 없습니다. DBA에게 문의해주세요."),
    USER_ADMIN_NOT_FOUND_EXCEPTION(NOT_FOUND, "해당 Admin이 존재하지 않습니다."),
    NOT_EQUAL_TRANSACTION_EXCEPTION(NOT_FOUND, "동일하지 않은 거래입니다."),
    NOT_FOUND_NOTIFICATION_TYPE_EXCEPTION(NOT_FOUND, "존재하지 않는 알림 타입 입니다"),
    NOT_FOUND_USER_SUBSCRIBE_EXCEPTION(NOT_FOUND, "유저의 구독정보가 존재하지 않습니다."),
    NOT_FOUND_NOTICE_EXCEPTION(NOT_FOUND, "공지가 존재하지 않습니다."),
    ADMIN_CONFIGURATION_NOT_FOUND_EXCEPTION(NOT_FOUND, "해당 설정이 존재하지 않습니다."),
    USER_DATA_NOT_FOUND_EXCEPTION(NOT_FOUND, "해당 유저 데이터가 존재하지 않습니다."),
    EVENT_REWARD_NOT_FOUND_EXCEPTION(NOT_FOUND, "해당 EventReward가 존재하지 않습니다."),
    EVENT_NOT_FOUND_EXCEPTION(NOT_FOUND, "해당 Event가 존재하지 않습니다."),
    EVENT_TIME_NOT_FOUND_EXCEPTION(NOT_FOUND, "해당 EventTime가 존재하지 않습니다."),
    IDEMPOTENCY_KEY_NOT_FOUND_EXCEPTION(NOT_FOUND, "멱등키의 이벤트가 존재하지 않습니다. 이벤트 참여를 먼저 해주세요"),
    EVENT_RANDOM_NOT_FOUND_EXCEPTION(NOT_FOUND, "해당 EventRandom가 존재하지 않습니다."),

    /**
     * 409 CONFLICT
     */
    ALREADY_EXIST_USER_EXCEPTION(CONFLICT, "이미 존재하는 유저입니다"),
    YELLOID_CONFLICT_USER_EXCEPTION(CONFLICT, "이미 존재하는 yelloId 입니다."),
    UUID_CONFLICT_USER_EXCEPTION(CONFLICT, "이미 존재하는 소셜 유저입니다."),
    DEVICE_TOKEN_CONFLICT_USER_EXCEPTION(CONFLICT, "이미 존재하는 deviceToken 입니다."),
    SUBSCRIBE_ACTIVE_EXCEPTION(CONFLICT, "이미 옐로플러스 구독한 유저입니다."),
    GOOGLE_SUBSCRIPTIONS_SUBSCRIPTION_EXCEPTION(CONFLICT, "이미 적용한 영수증입니다."),
    IDEMPOTENCY_KEY_CONFLICT_EXCEPTION(CONFLICT, "이미 존재하는 멱등키 입니다."),

    /**
     * 500 INTERNAL SERVER ERROR
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러가 발생했습니다"),
    REDIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis에 에러가 발생했습니다."),
    APPLE_TOKEN_SERVER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Apple과의 통신에 실패하였습니다."),
    GOOGLE_TOKEN_SERVER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Google과의 통신에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}