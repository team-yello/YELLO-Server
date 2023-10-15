package com.yello.server.domain.purchase.dto.request.google;

public record OneTimeProductNotification(
    String version,
    OneTimeProductNotificationType notificationType,
    String purchaseToken,
    String sku
) {

}
