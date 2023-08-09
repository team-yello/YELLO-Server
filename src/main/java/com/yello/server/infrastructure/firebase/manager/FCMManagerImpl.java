package com.yello.server.infrastructure.firebase.manager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.yello.server.infrastructure.firebase.dto.request.NotificationMessage;
import org.springframework.stereotype.Service;

@Service
public class FCMManagerImpl implements FCMManager {

    @Override
    public void send(Message message) {
        FirebaseMessaging.getInstance()
            .sendAsync(message);
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

}
