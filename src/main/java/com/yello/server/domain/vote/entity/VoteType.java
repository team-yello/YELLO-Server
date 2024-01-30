package com.yello.server.domain.vote.entity;

import java.text.MessageFormat;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VoteType {
    send("send");

    private final String intial;

    public static VoteType fromCode(String dbData) {
        return Arrays.stream(VoteType.values())
            .filter(v -> v.getIntial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("존재하지 않는 투표 타입입니다. {0}", dbData)));
    }

    public String intial() {
        return intial;
    }

}
