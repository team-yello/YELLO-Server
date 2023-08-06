package com.yello.server.small.global.firebase;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.yello.server.infrastructure.firebase.manager.FCMManager;

public class FakeFcmManger implements FCMManager {

    @Override
    public void send(Message message) {
        // send
    }

    @Override
    public Message createMessage(String deviceToken, Notification notification) {
        return Message.builder()
            .setToken(deviceToken)
            .setNotification(notification)
            .build();
    }

    public Message createMessage(String deviceToken, Notification notification, String path) {
        return Message.builder()
            .setToken(deviceToken)
            .setNotification(notification)
            .putData("path", path)
            .build();
    }
}
