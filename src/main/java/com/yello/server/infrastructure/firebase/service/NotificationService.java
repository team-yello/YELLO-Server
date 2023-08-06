package com.yello.server.infrastructure.firebase.service;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.vote.entity.Vote;

public interface NotificationService {

    void sendYelloNotification(Vote vote);

    void sendFriendNotification(Friend friend);

    void sendVoteAvailableNotification(Long receiverId);
}
