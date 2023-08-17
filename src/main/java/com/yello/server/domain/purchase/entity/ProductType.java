package com.yello.server.domain.purchase.entity;

import java.text.MessageFormat;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductType {
    YELLO_PLUS("yello_plus"),
    ONE_TICKET("one_ticket"),
    TWO_TICKET("two_ticket"),
    FIVE_TICKET("five_ticket");

    private final String intial;

    public static ProductType fromCode(String dbData) {
        return Arrays.stream(ProductType.values())
            .filter(v -> v.getIntial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("존재하지 않는 상품타입입니다. {0}", dbData)));
    }

    public String intial() {
        return intial;
    }

}
