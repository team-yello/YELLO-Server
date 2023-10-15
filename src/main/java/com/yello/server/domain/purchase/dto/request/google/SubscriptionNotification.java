package com.yello.server.domain.purchase.dto.request.google;

public record SubscriptionNotification(
    String version,
    SubscriptionNotificationType notificationType,
    String purchaseToken,
    String subscriptionId
) {

}
