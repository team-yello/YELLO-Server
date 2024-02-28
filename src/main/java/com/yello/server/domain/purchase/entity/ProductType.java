package com.yello.server.domain.purchase.entity;

import static com.yello.server.global.common.ErrorCode.ENUM_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.FIVE_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.GOOGLE_FIVE_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.GOOGLE_ONE_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.GOOGLE_TWO_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.GOOGLE_YELLO_PLUS_ID;
import static com.yello.server.global.common.util.ConstantUtil.ONE_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.TWO_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.YELLO_PLUS_ID;

import com.yello.server.global.exception.EnumIllegalArgumentException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductType {
    YELLO_PLUS("yello_plus"),
    ONE_TICKET("one_ticket"),
    TWO_TICKET("two_ticket"),
    FIVE_TICKET("five_ticket"),
    TEST("test");

    private final String initial;

    public static ProductType fromCode(String dbData) {
        return Arrays.stream(ProductType.values())
            .filter(v -> v.getInitial().equals(dbData))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }

    public static ProductType fromName(String name) {
        return Arrays.stream(ProductType.values())
            .filter(v -> v.name().equals(name))
            .findAny()
            .orElseThrow(() -> new EnumIllegalArgumentException(ENUM_BAD_REQUEST_EXCEPTION));
    }

    public static ProductType getProductType(String productId) {
        return switch (productId) {
            case GOOGLE_ONE_TICKET_ID, ONE_TICKET_ID -> ProductType.ONE_TICKET;
            case GOOGLE_TWO_TICKET_ID, TWO_TICKET_ID -> ProductType.TWO_TICKET;
            case GOOGLE_FIVE_TICKET_ID, FIVE_TICKET_ID -> ProductType.FIVE_TICKET;
            case GOOGLE_YELLO_PLUS_ID, YELLO_PLUS_ID -> ProductType.YELLO_PLUS;
            default -> null;
        };
    }

    public static Integer getTicketAmount(ProductType productType) {
        return switch (productType) {
            case ONE_TICKET -> 1;
            case TWO_TICKET -> 2;
            case FIVE_TICKET -> 5;
            default -> null;
        };
    }
}
