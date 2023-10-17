package com.yello.server.domain.purchase.dto.request.google;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public enum SubscriptionNotificationType {
    @SerializedName("1")
    SUBSCRIPTION_RECOVERED(1),
    @SerializedName("2")
    SUBSCRIPTION_RENEWED(2),
    @SerializedName("3")
    SUBSCRIPTION_CANCELED(3),
    @SerializedName("4")
    SUBSCRIPTION_PURCHASED(4),
    @SerializedName("5")
    SUBSCRIPTION_ON_HOLD(5),
    @SerializedName("6")
    SUBSCRIPTION_IN_GRACE_PERIOD(6),
    @SerializedName("7")
    SUBSCRIPTION_RESTARTED(7),
    @SerializedName("8")
    SUBSCRIPTION_PRICE_CHANGE_CONFIRMED(8),
    @SerializedName("9")
    SUBSCRIPTION_DEFERRED(9),
    @SerializedName("10")
    SUBSCRIPTION_PAUSED(10),
    @SerializedName("11")
    SUBSCRIPTION_PAUSE_SCHEDULE_CHANGED(11),
    @SerializedName("12")
    SUBSCRIPTION_REVOKED(12),
    @SerializedName("13")
    SUBSCRIPTION_EXPIRED(13);

    private final Integer value;

    SubscriptionNotificationType(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
