package com.yello.server.infrastructure.firebase.service;

import com.google.firebase.messaging.Message;
import com.yello.server.domain.admin.repository.UserAdminRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.global.common.dto.EmptyObject;
import com.yello.server.infrastructure.firebase.dto.request.NotificationCustomMessage;
import com.yello.server.infrastructure.firebase.dto.request.NotificationMessage;
import com.yello.server.infrastructure.firebase.manager.FCMManager;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Log4j2
@Builder
@Service
@RequiredArgsConstructor
public class NotificationFcmService implements NotificationService {

    private final FCMManager fcmManager;
    private final UserAdminRepository userAdminRepository;
    private final UserRepository userRepository;

    @Override
    public void sendRecommendNotification(User user, User target) {
        NotificationMessage notificationMessage =
                NotificationMessage.toRecommendNotificationContent(user);

        if (target.getDeviceToken() != null && !Objects.equals(target.getDeviceToken(), "")) {
            final Message message =
                    fcmManager.createMessage(target.getDeviceToken(), notificationMessage);
            fcmManager.send(message);
        }
    }

    @Override
    public void sendYelloNotification(Vote vote) {
        final User receiver = vote.getReceiver();

        NotificationMessage notificationMessage =
                NotificationMessage.toYelloNotificationContent(vote);

        final String path = "/api/v1/vote/" + vote.getId().toString();

        if (receiver.getDeviceToken() != null && !Objects.equals(receiver.getDeviceToken(), "")) {
            final Message message =
                    fcmManager.createMessage(receiver.getDeviceToken(), notificationMessage, path);
            fcmManager.send(message);
        }
    }

    @Override
    public void sendFriendNotification(Friend friend) {
        final User receiver = friend.getTarget();
        final User sender = friend.getUser();

        NotificationMessage notificationMessage =
                NotificationMessage.toFriendNotificationContent(sender);

        if (receiver.getDeviceToken() != null && !Objects.equals(receiver.getDeviceToken(), "")) {
            final Message message =
                    fcmManager.createMessage(receiver.getDeviceToken(), notificationMessage);
            fcmManager.send(message);
        }
    }

    @Override
    public void sendVoteAvailableNotification(Long receiverId) {
        final User receiveUser = userRepository.getById(receiverId);

        NotificationMessage notificationMessage =
                NotificationMessage.toVoteAvailableNotificationContent();

        if (receiveUser.getDeviceToken() != null && !Objects.equals(receiveUser.getDeviceToken(),
                "")) {
            final Message message =
                    fcmManager.createMessage(receiveUser.getDeviceToken(), notificationMessage);
            fcmManager.send(message);
            log.info("[rabbitmq] successfully send notification!");
        }
    }

    @Override
    public void sendCustomNotification(NotificationCustomMessage request) {

        request.userIdList().stream()
                .forEach(userId -> {
                    final User receiver = userRepository.getById(userId);

                    NotificationMessage notificationMessage =
                            NotificationMessage.toYelloNotificationCustomContent(request);

                    if (receiver.getDeviceToken() != null && !Objects.equals(receiver.getDeviceToken(),
                            "")) {
                        final Message message =
                                fcmManager.createMessage(receiver.getDeviceToken(), notificationMessage);
                        fcmManager.send(message);
                    }
                });

    }

    @Override
    public EmptyObject adminSendCustomNotification(Long adminId, NotificationCustomMessage request) {
        // exception
        final User admin = userRepository.getById(adminId);
        userAdminRepository.getByUser(admin);

        // logic
        this.sendCustomNotification(request);

        return EmptyObject.builder().build();
    }

    @Override
    public void sendLunchEventNotification(User user) {
        System.out.println(user.getId() + " asfsfsdfsd");
        final User receiver = userRepository.getById(user.getId());

        NotificationMessage notificationMessage =
                NotificationMessage.toAllUserLunchEventNotificationContent();

        if (receiver.getDeviceToken() != null && !Objects.equals(receiver.getDeviceToken(),
                "")) {
            final Message message =
                    fcmManager.createMessage(receiver.getDeviceToken(), notificationMessage);
            fcmManager.send(message);

        }
    }

    @Override
    public void sendOpenVoteNotification(User sender) {
        NotificationMessage notificationMessage =
                NotificationMessage.toUserOpenVoteNotificationContent(sender);

        if (sender.getDeviceToken() != null && !Objects.equals(sender.getDeviceToken(), "")) {
            final Message message =
                    fcmManager.createMessage(sender.getDeviceToken(), notificationMessage);
            fcmManager.send(message);
        }
    }

    @Override
    public void sendRecommendSignupAndGetTicketNotification(User recommendUser) {
        NotificationMessage notificationMessage =
                NotificationMessage.toUserAndFriendRecommendSignupAndGetTicketNotificationContent(recommendUser);

        if (recommendUser.getDeviceToken() != null && !Objects.equals(recommendUser.getDeviceToken(), "")) {
            final Message message =
                    fcmManager.createMessage(recommendUser.getDeviceToken(), notificationMessage);
            fcmManager.send(message);
        }
    }
}
