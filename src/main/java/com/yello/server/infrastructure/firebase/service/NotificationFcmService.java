package com.yello.server.infrastructure.firebase.service;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.infrastructure.firebase.dto.request.NotificationRequest;
import com.yello.server.infrastructure.firebase.manager.FCMManager;
import com.yello.server.infrastructure.redis.repository.TokenRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Builder
@Service
@RequiredArgsConstructor
public class NotificationFcmService implements NotificationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final FCMManager fcmManager;

    @Override
    public void sendNotification(NotificationRequest notificationRequest) {

        final User user = userRepository.getById(notificationRequest.targetId());
        final String deviceToken = tokenRepository.getDeviceToken(user.getUuid());
        final Notification notification = notificationRequest.toNotification();
        final Message message = fcmManager.createMessage(deviceToken, notification);

        fcmManager.send(message);
    }
}
