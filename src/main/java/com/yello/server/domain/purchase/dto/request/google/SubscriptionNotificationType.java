package com.yello.server.domain.purchase.dto.request.google;

import lombok.Getter;

@Getter
public enum SubscriptionNotificationType {
    SUBSCRIPTION_RECOVERED(1),
    SUBSCRIPTION_RENEWED(2),
    SUBSCRIPTION_CANCELED(3),
    SUBSCRIPTION_PURCHASED(4),
    SUBSCRIPTION_ON_HOLD(5),
    SUBSCRIPTION_IN_GRACE_PERIOD(6),
    SUBSCRIPTION_RESTARTED(7),
    SUBSCRIPTION_PRICE_CHANGE_CONFIRMED(8),
    SUBSCRIPTION_DEFERRED(9),
    SUBSCRIPTION_PAUSED(10),
    SUBSCRIPTION_PAUSE_SCHEDULE_CHANGED(11),
    SUBSCRIPTION_REVOKED(12),
    SUBSCRIPTION_EXPIRED(13);

    private final Long value;

    SubscriptionNotificationType(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
