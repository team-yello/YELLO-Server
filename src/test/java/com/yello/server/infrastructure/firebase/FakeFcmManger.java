package com.yello.server.infrastructure.firebase;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.Message;
import com.yello.server.domain.vote.FakeVoteRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.infrastructure.firebase.dto.request.NotificationMessage;
import com.yello.server.infrastructure.firebase.manager.FCMManager;

public class FakeFcmManger implements FCMManager {

    private final VoteRepository voteRepository = new FakeVoteRepository();

    @Override
    public void send(Message message) {
        // send
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
        final Integer unreadVoteCounts = voteRepository.countUnreadByReceiverDeviceToken(deviceToken);

        return Message.builder()
            .setToken(deviceToken)
            .setNotification(notificationMessage.toNotification())
            .setApnsConfig(
                ApnsConfig.builder()
                    .setAps(
                        Aps.builder()
                            .setBadge(unreadVoteCounts)
                            .setSound("default")
                            .build()
                    )
                    .build()
            )
            .putData("type", notificationMessage.type().name())
            .putData("path", path)
            .build();
    }

}