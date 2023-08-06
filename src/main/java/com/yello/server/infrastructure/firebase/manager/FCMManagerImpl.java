package com.yello.server.infrastructure.firebase.manager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FCMManagerImpl implements FCMManager {

    @Override
    public void send(Message message) {
        FirebaseMessaging.getInstance()
            .sendAsync(message);
    }

    @Override
    public Message createMessage(String deviceToken, Notification notification) {
        return Message.builder()
            .setToken(deviceToken)
            .setNotification(notification)
            .build();
    }

    @Override
    public Message createMessage(String deviceToken, Notification notification, String path) {
        return Message.builder()
            .setToken(deviceToken)
            .setNotification(notification)
            .putData("path", path)
            .build();
    }

}
