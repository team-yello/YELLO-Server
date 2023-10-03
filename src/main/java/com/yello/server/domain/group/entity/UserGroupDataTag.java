package com.yello.server.domain.group.entity;

import java.text.MessageFormat;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserGroupDataTag {
    ADDRESS("ADDRESS"),
    POPULATION("POPULATION");

    private final String intial;

    public static UserGroupDataTag fromCode(String dbData) {
        return Arrays.stream(UserGroupDataTag.values())
            .filter(v -> v.getIntial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("존재하지 않는 그룹 정보 key 타입 입니다. {0}", dbData)));
    }

    public String intial() {
        return intial;
    }
}
