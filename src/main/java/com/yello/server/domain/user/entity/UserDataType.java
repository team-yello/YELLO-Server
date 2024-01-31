package com.yello.server.domain.user.entity;

import static com.yello.server.global.common.ErrorCode.ENUM_BAD_REQUEST_EXCEPTION;

import com.yello.server.global.exception.EnumIllegalArgumentException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserDataType {
    /**
     * ACCOUNT_UPDATE_AT, RECOMMENDED DotaTimeFormatter.ISO_OFFSET_DATE_TIME 으로 저장해주세요
     */
    WITHDRAW_REASON(String.class, "withdraw-reason"),
    ACCOUNT_UPDATED_AT(ZonedDateTime.class, "account-updated-at"),
    RECOMMENDED(ZonedDateTime.class, "recommended");

    private final Class<?> classType;
    private final String initial;

    public static UserDataType fromCode(String dbData) {
        return Arrays.stream(UserDataType.values())
            .filter(v -> v.getInitial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }

    public static UserDataType fromName(String name) {
        return Arrays.stream(UserDataType.values())
            .filter(v -> v.name().equals(name))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }
}
