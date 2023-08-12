package com.yello.server.domain.purchase.entity;

import java.text.MessageFormat;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gateway {
    GOOGLE("google"),
    APPLE("apple");

    private final String intial;

    public static Gateway fromCode(String dbData) {
        return Arrays.stream(Gateway.values())
            .filter(v -> v.getIntial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("존재하지 않는 게이트웨이입니다. {0}", dbData)));
    }

    public String intial() {
        return intial;
    }

}
