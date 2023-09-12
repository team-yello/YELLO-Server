package com.yello.server.domain.purchase.dto.apple;

import lombok.Builder;

@Builder
public record AppleNotificationPayloadVO(
    String notificationType,
    String subType,
    ApplePayloadDataVO data,
    String notificationUUID

) {

    public static AppleNotificationPayloadVO of(String notificationType, String subType,
        ApplePayloadDataVO data, String notificationUUID) {
        return AppleNotificationPayloadVO.builder()
            .notificationType(notificationType)
            .subType(subType)
            .data(data)
            .notificationUUID(notificationUUID)
            .build();
    }

}
