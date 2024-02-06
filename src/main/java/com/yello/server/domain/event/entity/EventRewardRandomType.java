package com.yello.server.domain.event.entity;

import static com.yello.server.global.common.ErrorCode.ENUM_BAD_REQUEST_EXCEPTION;

import com.yello.server.global.exception.EnumIllegalArgumentException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventRewardRandomType {
    RANDOM("RANDOM"),
    FIXED("FIXED");

    private final String initial;

    public static EventRewardRandomType fromCode(String dbData) {
        return Arrays.stream(EventRewardRandomType.values())
            .filter(v -> v.getInitial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }

    public static EventRewardRandomType fromName(String name) {
        return Arrays.stream(EventRewardRandomType.values())
            .filter(v -> v.name().equals(name))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }
}
