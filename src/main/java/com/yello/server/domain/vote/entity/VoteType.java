package com.yello.server.domain.vote.entity;

import static com.yello.server.global.common.ErrorCode.ENUM_BAD_REQUEST_EXCEPTION;

import com.yello.server.global.exception.EnumIllegalArgumentException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VoteType {
    SEND("send");

    private final String initial;

    public static VoteType fromCode(String value) {
        return Arrays.stream(VoteType.values())
            .filter(v -> v.getInitial().equals(value))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }

    public static VoteType fromName(String name) {
        return Arrays.stream(VoteType.values())
            .filter(v -> v.name().equals(name))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }
}
