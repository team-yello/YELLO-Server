package com.yello.server.domain.friend.service;

import org.springframework.transaction.annotation.Transactional;

public interface FriendService {
    void addFriend(Long userId, Long friendId);
}
