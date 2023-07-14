package com.yello.server.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("M"),
    FEMALE("F");

    private final String intial;

    public String intial() {
        return intial;
    }

    public static Gender fromCode(String dbData) {
        return Arrays.stream(Gender.values())
                .filter(v -> v.getIntial().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("존재하지 않는 성별입니다.", dbData)));
    }

}
