package com.yello.server.domain.purchase.entity;

import java.text.MessageFormat;
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

    private final String intial;

    public static PurchaseState fromCode(String dbData) {
        return Arrays.stream(PurchaseState.values())
            .filter(v -> v.getIntial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("존재하지 않는 결제 상태입니다. {0}", dbData)));
    }

    public String intial() {
        return intial;
    }
}
