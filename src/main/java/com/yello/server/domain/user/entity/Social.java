package com.yello.server.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
@Getter
@RequiredArgsConstructor
public enum Social {
    KAKAO("Kakao"),
    APPLE("Apple");

    private final String intial;

    public String intial() {
        return intial;
    }

    public static Social fromCode(String dbData) {
        return Arrays.stream(Social.values())
                .filter(v -> v.getIntial().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("존재하지 않는 social 입니다.", dbData)));
    }

}
