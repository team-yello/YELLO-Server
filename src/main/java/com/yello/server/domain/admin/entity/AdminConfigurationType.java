package com.yello.server.domain.admin.entity;

import static com.yello.server.global.common.ErrorCode.ENUM_BAD_REQUEST_EXCEPTION;

import com.yello.server.global.exception.EnumIllegalArgumentException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminConfigurationType {
    ACCESS_TOKEN_TIME("ACCESS_TOKEN_TIME"),
    REFRESH_TOKEN_TIME("REFRESH_TOKEN_TIME"),
    ADMIN_SITE_PASSWORD("ADMIN_SITE_PASSWORD");

    private final String initial;

    public static AdminConfigurationType fromCode(String dbData) {
        return Arrays.stream(AdminConfigurationType.values())
            .filter(v -> v.getInitial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }

    public static AdminConfigurationType fromName(String name) {
        return Arrays.stream(AdminConfigurationType.values())
            .filter(v -> v.name().equals(name))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }
}
