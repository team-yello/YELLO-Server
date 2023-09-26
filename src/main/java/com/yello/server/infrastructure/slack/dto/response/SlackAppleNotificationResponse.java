package com.yello.server.infrastructure.slack.dto.response;

import com.yello.server.domain.purchase.dto.apple.AppleNotificationPayloadVO;
import com.yello.server.domain.purchase.entity.Purchase;
import lombok.Builder;

@Builder
public record SlackAppleNotificationResponse(
    String notificationType,
    String subtype,
    String transactionId,
    Long userId,
    String userName,
    String yelloId

) {

    public static SlackAppleNotificationResponse of(AppleNotificationPayloadVO payloadVO,
        Purchase purchase) {
        return SlackAppleNotificationResponse.builder()
            .notificationType(payloadVO.notificationType())
            .subtype(payloadVO.subtype())
            .transactionId(purchase.getTransactionId())
            .userId(purchase.getUser().getId())
            .userName(purchase.getUser().getName())
            .yelloId(purchase.getUser().getYelloId())
            .build();
    }

}
