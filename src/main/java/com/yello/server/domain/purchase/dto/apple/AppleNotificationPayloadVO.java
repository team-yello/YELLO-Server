package com.yello.server.domain.purchase.dto.apple;

import lombok.Builder;

@Builder
public record AppleNotificationPayloadVO(
    String notificationType,
    String subType,
    ApplePayloadDataVO data,
    String notificationUUID

) {

}
