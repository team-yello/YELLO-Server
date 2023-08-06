package com.yello.server.infrastructure.firebase.service;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.infrastructure.firebase.dto.request.NotificationMessage;
import com.yello.server.infrastructure.firebase.manager.FCMManager;
import com.yello.server.infrastructure.redis.repository.TokenRepository;
import java.text.MessageFormat;
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
    public void sendYelloNotification(Vote vote) {
        final User receiveUser = vote.getReceiver();
        final String deviceToken = tokenRepository.getDeviceToken(receiveUser.getUuid());
        final Notification notification = NotificationMessage.toYelloNotificationContent(receiveUser)
            .toNotification();
        final String path = MessageFormat.format("/api/v1/vote/{0}", vote.getId());
        final Message message = fcmManager.createMessage(deviceToken, notification, path);

        fcmManager.send(message);
    }

    @Override
    public void sendFriendNotification(Friend friend) {
        final User receiveUser = friend.getTarget();
        final String deviceToken = tokenRepository.getDeviceToken(receiveUser.getUuid());
        final Notification notification = NotificationMessage.toFriendNotificationContent(receiveUser)
            .toNotification();

        final Message message = fcmManager.createMessage(deviceToken, notification);

        fcmManager.send(message);
    }

    @Override
    public void sendVoteAvailableNotification(Long receiverId) {
        final User receiveUser = userRepository.getById(receiverId);
        final String deviceToken = tokenRepository.getDeviceToken(receiveUser.getUuid());
        final Notification notification = NotificationMessage.toVoteAvailableNotificationContent()
            .toNotification();

        final Message message = fcmManager.createMessage(deviceToken, notification);

        fcmManager.send(message);
    }
}
