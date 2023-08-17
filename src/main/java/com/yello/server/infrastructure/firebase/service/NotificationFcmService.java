package com.yello.server.infrastructure.firebase.service;

import com.google.firebase.messaging.Message;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.infrastructure.firebase.dto.request.NotificationMessage;
import com.yello.server.infrastructure.firebase.manager.FCMManager;
import com.yello.server.infrastructure.redis.repository.TokenRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Builder
@Service
@RequiredArgsConstructor
public class NotificationFcmService implements NotificationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final FCMManager fcmManager;

    @Override
    public void sendYelloNotification(Vote vote) {
        final User receiver = vote.getReceiver();

        NotificationMessage notificationMessage = NotificationMessage.toYelloNotificationContent(vote);

        final String path = "/api/v1/vote/" + vote.getId().toString();
        final Message message = fcmManager.createMessage(receiver.getDeviceToken(), notificationMessage, path);

        fcmManager.send(message);
    }

    @Override
    public void sendFriendNotification(Friend friend) {
        final User receiver = friend.getTarget();
        final User sender = friend.getUser();

        NotificationMessage notificationMessage = NotificationMessage.toFriendNotificationContent(sender);

        final Message message = fcmManager.createMessage(receiver.getDeviceToken(), notificationMessage);

        fcmManager.send(message);
    }

    @Override
    public void sendVoteAvailableNotification(Long receiverId) {
        final User receiveUser = userRepository.getById(receiverId);

        NotificationMessage notificationMessage = NotificationMessage.toVoteAvailableNotificationContent();

        final Message message = fcmManager.createMessage(receiveUser.getDeviceToken(), notificationMessage);

        fcmManager.send(message);
        log.info("[rabbitmq] successfully send notification!");
    }
}
