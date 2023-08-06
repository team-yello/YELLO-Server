package com.yello.server.infrastructure.firebase.service;

import com.yello.server.infrastructure.firebase.dto.request.NotificationRequest;

public interface NotificationService {

    void sendNotification(NotificationRequest notificationRequest);

}
