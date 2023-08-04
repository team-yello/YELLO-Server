package com.yello.server.domain.user.entity;

import java.text.MessageFormat;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Social {
    KAKAO("Kakao"),
    APPLE("Apple");

    private final String intial;

    public static Social fromCode(String dbData) {
        return Arrays.stream(Social.values())
            .filter(v -> v.getIntial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("존재하지 않는 소셜입니다. {0}", dbData)));
    }

    public String intial() {
        return intial;
    }

}
