package com.yello.server.infrastructure.firebase.manager;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

public interface FCMManager {

    void send(Message message);

    Message createMessage(String deviceToken, Notification notification);

    Message createMessage(String deviceToken, Notification notification, String path);

}
