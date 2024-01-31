package com.yello.server.domain.group.entity;

import static com.yello.server.global.common.ErrorCode.ENUM_BAD_REQUEST_EXCEPTION;

import com.yello.server.global.exception.EnumIllegalArgumentException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserGroupDataTag {
    ADDRESS("ADDRESS"),
    POPULATION("POPULATION");

    private final String initial;

    public static UserGroupDataTag fromCode(String dbData) {
        return Arrays.stream(UserGroupDataTag.values())
            .filter(v -> v.getInitial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }

    public static UserGroupDataTag fromName(String name) {
        return Arrays.stream(UserGroupDataTag.values())
            .filter(v -> v.name().equals(name))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }
}
