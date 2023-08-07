package com.yello.server.domain.user.entity;

import java.text.MessageFormat;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Subscribe {
    NORMAL("normal"),
    ACTIVE("active"),
    CANCELED("canceled");

    private final String intial;

    public static Subscribe fromCode(String dbData) {
        return Arrays.stream(Subscribe.values())
            .filter(v -> v.getIntial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("잘못된 구독입니다. {0}", dbData)));
    }

    public String intial() {
        return intial;
    }

}
