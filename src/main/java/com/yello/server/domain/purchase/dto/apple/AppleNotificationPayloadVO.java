package com.yello.server.domain.purchase.dto.apple;

import lombok.Builder;

@Builder
public record AppleNotificationPayloadVO(
    String notificationType,
    String subtype,
    ApplePayloadDataVO data,
    String notificationUUID

) {

    public static AppleNotificationPayloadVO of(String notificationType, String subtype,
        ApplePayloadDataVO data, String notificationUUID) {
        return AppleNotificationPayloadVO.builder()
            .notificationType(notificationType)
            .subtype(subtype)
            .data(data)
            .notificationUUID(notificationUUID)
            .build();
    }

}
