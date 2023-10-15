package com.yello.server.domain.purchase.dto.request.google;

import lombok.Getter;

@Getter
public enum OneTimeProductNotificationType {
    ONE_TIME_PRODUCT_PURCHASED(1),
    ONE_TIME_PRODUCT_CANCELED(2);

    private final Long value;

    OneTimeProductNotificationType(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
