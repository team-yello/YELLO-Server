package com.yello.server.infrastructure.firebase.dto.request;

import com.google.firebase.messaging.Notification;
import lombok.Builder;

@Builder
public record NotificationRequest(
    Long targetId,
    String title,
    String message
) {

    public Notification toNotification() {
        return Notification.builder()
            .setTitle(title())
            .setBody(message())
            .build();
    }
}
