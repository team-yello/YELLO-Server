package com.yello.server.domain.admin.entity;

import java.text.MessageFormat;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminConfigurationType {
    ACCESS_TOKEN_TIME("ACCESS_TOKEN_TIME"),
    REFRESH_TOKEN_TIME("REFRESH_TOKEN_TIME"),
    ADMIN_SITE_PASSWORD("ADMIN_SITE_PASSWORD");

    private final String intial;

    public static AdminConfigurationType fromCode(String dbData) {
        return Arrays.stream(AdminConfigurationType.values())
            .filter(v -> v.getIntial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("존재하지 않는 태그입니다. {0}", dbData)));
    }

    public String intial() {
        return intial;
    }
}
