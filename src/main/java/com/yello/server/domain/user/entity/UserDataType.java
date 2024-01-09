package com.yello.server.domain.user.entity;

import java.text.MessageFormat;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserDataType {
    WITHDRAW_REASON("WITHDRAW_REASON"),
    ACCOUNT_UPDATED_AT("ACCOUNT_UPDATED_AT"),
    RECOMMENDED("RECOMMENDED");
    
    private final String intial;

    public static UserDataType fromCode(String dbData) {
        return Arrays.stream(UserDataType.values())
            .filter(v -> v.getIntial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("존재하지 않는 유저데이터 타입입니다. {0}", dbData)));
    }

    public String intial() {
        return intial;
    }
}
