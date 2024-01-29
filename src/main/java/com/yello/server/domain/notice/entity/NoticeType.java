package com.yello.server.domain.notice.entity;

import java.text.MessageFormat;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoticeType {
    NOTICE("NOTICE"),
    BANNER("BANNER");

    private final String intial;

    public static NoticeType fromCode(String dbData) {
        return Arrays.stream(NoticeType.values())
            .filter(v -> v.getIntial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("존재하지 않는 소셜입니다. {0}", dbData)));
    }

    public String intial() {
        return intial;
    }

}
