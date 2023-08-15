package com.yello.server.infrastructure.firebase.manager;

import com.google.firebase.messaging.Message;
import com.yello.server.infrastructure.firebase.dto.request.NotificationMessage;

public interface FCMManager {

    void send(Message message);

    Message createMessage(String deviceToken, NotificationMessage notificationMessage);

    Message createMessage(String deviceToken, NotificationMessage notificationMessage, String path);

    Message createMessageWithOptions(String deviceToken, NotificationMessage notificationMessage, String path);

}
