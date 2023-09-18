package com.yello.server.domain.group.entity;

import java.text.MessageFormat;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SchoolType {
    UNIVERSITY("UNIVERSITY"),
    HIGHSCHOOL("HIGH_SCHOOL");

    private final String intial;

    public static SchoolType fromCode(String dbData) {
        return Arrays.stream(SchoolType.values())
            .filter(v -> v.getIntial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("존재하지 않는 학교타입 입니다. {0}", dbData)));
    }

    public String intial() {
        return intial;
    }

}
