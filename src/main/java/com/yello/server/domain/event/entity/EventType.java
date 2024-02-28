package com.yello.server.domain.event.entity;

import static com.yello.server.global.common.ErrorCode.ENUM_BAD_REQUEST_EXCEPTION;

import com.yello.server.global.exception.EnumIllegalArgumentException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {
    ADMOB_POINT("ADMOB_POINT"),
    ADMOB_MULTIPLE_POINT("ADMOB_MULTIPLE_POINT"),
    LUNCH_EVENT("LUNCH_EVENT");

    private final String initial;

    public static EventType fromCode(String dbData) {
        return Arrays.stream(EventType.values())
            .filter(v -> v.getInitial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }

    public static EventType fromName(String name) {
        return Arrays.stream(EventType.values())
            .filter(v -> v.name().equals(name))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }
}
