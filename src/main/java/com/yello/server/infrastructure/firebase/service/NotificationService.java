package com.yello.server.infrastructure.firebase.service;

import com.yello.server.domain.user.entity.User;
import com.yello.server.infrastructure.firebase.dto.request.NotificationMessage;

public interface NotificationService {

    void sendNotification(User user, NotificationMessage notificationMessageVO);

}
