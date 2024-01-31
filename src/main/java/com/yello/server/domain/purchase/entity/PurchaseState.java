package com.yello.server.domain.purchase.entity;

import static com.yello.server.global.common.ErrorCode.ENUM_BAD_REQUEST_EXCEPTION;

import com.yello.server.global.exception.EnumIllegalArgumentException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PurchaseState {
    ACTIVE("ACTIVE"),
    CANCELED("CANCELED"),
    PAUSED("PAUSED"),
    INACTIVE("INACTIVE");

    private final String initial;

    public static PurchaseState fromCode(String dbData) {
        return Arrays.stream(PurchaseState.values())
            .filter(v -> v.getInitial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }

    public static PurchaseState fromName(String name) {
        return Arrays.stream(PurchaseState.values())
            .filter(v -> v.name().equals(name))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }
}
