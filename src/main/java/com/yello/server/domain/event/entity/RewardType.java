package com.yello.server.domain.event.entity;

import static com.yello.server.global.common.ErrorCode.ENUM_BAD_REQUEST_EXCEPTION;

import com.yello.server.global.exception.EnumIllegalArgumentException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RewardType {
    POINT("POINT"),
    TICKET("TICKET"),
    ADMOB_POINT("ADMOB_POINT"),
    ADMOB_MULTIPLE_POINT("ADMOB_MULTIPLE_POINT");

    private final String initial;

    public static RewardType fromCode(String dbData) {
        return Arrays.stream(RewardType.values())
            .filter(v -> v.getInitial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }

    public static RewardType fromName(String name) {
        return Arrays.stream(RewardType.values())
            .filter(v -> v.name().equals(name))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }
}
