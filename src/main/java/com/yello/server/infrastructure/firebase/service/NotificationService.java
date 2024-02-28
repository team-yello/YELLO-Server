package com.yello.server.infrastructure.firebase.service;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.global.common.dto.EmptyObject;
import com.yello.server.infrastructure.firebase.dto.request.NotificationCustomMessage;

import java.util.List;

public interface NotificationService {

    void sendRecommendNotification(User user, User target);

    void sendYelloNotification(Vote vote);

    void sendFriendNotification(Friend friend);

    void sendVoteAvailableNotification(Long receiverId);

    void sendCustomNotification(NotificationCustomMessage request);

    EmptyObject adminSendCustomNotification(Long adminId, NotificationCustomMessage request);
    void sendLunchEventNotification(User userList);

    void sendOpenVoteNotification(User user);

    void sendRecommendSignupNotification(User user);
}
