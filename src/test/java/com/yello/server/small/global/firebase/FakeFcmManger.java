package com.yello.server.small.global.firebase;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.Message;
import com.yello.server.infrastructure.firebase.dto.request.NotificationMessage;
import com.yello.server.infrastructure.firebase.manager.FCMManager;

public class FakeFcmManger implements FCMManager {

    @Override
    public void send(Message message) {
        // send
    }

    @Override
    public Message createMessage(String deviceToken, NotificationMessage notificationMessage) {
        return Message.builder()
            .setToken(deviceToken)
            .setNotification(notificationMessage.toNotification())
            .putData("type", notificationMessage.type().name())
            .build();
    }

    @Override
    public Message createMessage(String deviceToken, NotificationMessage notificationMessage, String path) {
        return Message.builder()
            .setToken(deviceToken)
            .setNotification(notificationMessage.toNotification())
            .putData("type", notificationMessage.type().name())
            .putData("path", path)
            .build();
    }

    @Override
    public Message createMessageWithOptions(String deviceToken, NotificationMessage notificationMessage, String path) {
        AndroidConfig androidConfig = AndroidConfig.builder()
            .putData("id", "type")
            .putData("aos_silent", "silent")
            .build();

        ApnsConfig apnsConfig = ApnsConfig.builder()
            .setAps(Aps.builder()
                .setContentAvailable(true)
                .putCustomData("apns-push-type", "background")
                .putCustomData("apns-priority", "5")
                .build())
            .build();

        return Message.builder()
            .setToken(deviceToken)
            .setNotification(notificationMessage.toNotification())
            .putData("type", notificationMessage.type().name())
            .putData("path", path)
            .setAndroidConfig(androidConfig)
            .setApnsConfig(apnsConfig)
            .build();
    }
}
