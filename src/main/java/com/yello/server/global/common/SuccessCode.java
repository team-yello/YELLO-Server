package com.yello.server.global.common;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

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
    READ_VOTE_SUCCESS(OK, "투표 조회에 성공했습니다."),
    YELLOID_VALIDATION_SUCCESS(OK, "옐로 아이디 중복 여부 조회에 성공했습니다."),
    READ_FRIEND_SUCCESS(OK, "친구 조회에 성공했습니다."),
    READ_USER_SUCCESS(OK, "유저 조회에 성공했습니다."),
    USER_SUBSCRIBE_NEEDED_READ_SUCCESS(OK, "유저 구독 연장 유도 필요 여부 확인 조회에 성공하였습니다."),
    USER_PURCHASE_INFO_READ_SUCCESS(OK, "유저 결제 정보 조회에 성공하였습니다"),
    ADD_FRIEND_SUCCESS(OK, "친구 추가에 성공했습니다."),
    SHUFFLE_FRIEND_SUCCESS(OK, "친구 셔플에 성공했습니다."),
    CHECK_KEYWORD_SUCCESS(OK, "키워드 확인에 성공했습니다."),
    READ_YELLO_VOTE_SUCCESS(OK, "투표 질문 리스트 조회에 성공했습니다."),
    READ_YELLO_START_SUCCESS(OK, "투표 시작하기에 성공했습니다."),
    UPDATE_DEVICE_TOKEN_USER_SUCCESS(OK, "deviceToken 업데이트에 성공하였습니다"),
    DELETE_USER_SUCCESS(OK, "유저 탈퇴에 성공했습니다."),
    DELETE_FRIEND_SUCCESS(OK, "친구 삭제에 성공했습니다."),
    ONBOARDING_FRIENDS_SUCCESS(OK, "추천 친구 조회에 성공했습니다."),
    UNIVERSITY_NAME_SEARCH_USER_GROUP_SUCCESS(OK, "학교 이름 검색에 성공했습니다."),
    DEPARTMENT_NAME_SEARCH_BY_SCHOOL_NAME_SCHOOL_SUCCESS(OK, "학과 검색에 성공했습니다."),
    REVEAL_NAME_HINT_SUCCESS(OK, "이름 초성 확인에 성공했습니다."),
    REVEAL_NAME_SUCCESS(OK, "전체 이름 확인에 성공했습니다."),
    FRIEND_SEARCH_SUCCESS(OK, "친구 검색하기에 성공했습니다."),
    VERIFY_RECEIPT_SUCCESS(OK, "인앱결제 검증에 성공했습니다."),
    READ_USER_ADMIN_SUCCESS(OK, "어드민 페이지 유저 조회에 성공하였습니다."),
    READ_USER_DETAIL_ADMIN_SUCCESS(OK, "어드민 페이지 유저 상세 조회에 성공하였습니다."),
    UPDATE_USER_DETAIL_ADMIN_SUCCESS(OK, "어드민 페이지 유저 상세 정보 수정에 성공하였습니다."),
    READ_COOLDOWN_ADMIN_SUCCESS(OK, "어드민 페이지 쿨다운 조회에 성공하였습니다."),
    DELETE_USER_ADMIN_SUCCESS(OK, "어드민 권환으로 유저 삭제에 성공하였습니다."),
    DELETE_COOLDOWN_ADMIN_SUCCESS(OK, "어드민 권환으로 쿨다운 삭제에 성공하였습니다."),
    POST_APPLE_NOTIFICATION_SUCCESS(OK, "apple 알림 처리에 성공하였습니다."),
    POST_GOOGLE_NOTIFICATION_SUCCESS(OK, "google 알림 처리에 성공하였습니다."),
    READ_QUESTION_ADMIN_SUCCESS(OK, "어드민 페이지 질문 조회에 성공하였습니다."),
    READ_QUESTION_DETAIL_ADMIN_SUCCESS(OK, "어드민 페이지 질문 상세 조회에 성공하였습니다."),
    DELETE_QUESTION_ADMIN_SUCCESS(OK, "어드민 권환으로 질문지 삭제에 성공하였습니다."),
    CLASS_NAME_SEARCH_BY_SCHOOL_NAME_SCHOOL_SUCCESS(OK, "학반 검색에 성공했습니다."),
    READ_USER_SUBSCRIBE_SUCCESS(OK, "구독 정보 조회에 성공하였습니다."),
    READ_NOTICE_SUCCESS(OK, "공지 조회에 성공하였습니다."),

    /**
     * 201 CREATED
     */
    SIGNUP_SUCCESS(CREATED, "회원가입이 완료됐습니다."),
    CREATE_BOARD_SUCCESS(CREATED, "게시물 생성이 완료됐습니다."),
    LOGIN_SUCCESS(CREATED, "로그인이 성공했습니다."),
    SIGN_UP_SUCCESS(CREATED, "회원 가입에 성공했습니다."),
    CREATE_VOTE_SUCCESS(CREATED, "투표를 성공했습니다."),
    CREATE_PAY_COUNT(CREATED, "이용권 버튼 클릭 횟수가 저장에 성공했습니다."),
    RE_ISSUE_TOKEN_AUTH_SUCCESS(CREATED, "토큰 재발급에 성공했습니다."),
    PURCHASE_SUBSCRIPTION_VERIFY_SUCCESS(CREATED, "애플 결제 검증 및 반영에 성공하였습니다."),
    GOOGLE_PURCHASE_SUBSCRIPTION_VERIFY_SUCCESS(CREATED, "구글 구독 결제 검증 및 반영에 성공하였습니다."),
    GOOGLE_PURCHASE_INAPP_VERIFY_SUCCESS(CREATED, "구글 인앱 결제 검증 및 반영에 성공하였습니다."),
    LOGIN_USER_ADMIN_SUCCESS(CREATED, "어드민 로그인에 성공하였습니다.");


    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
