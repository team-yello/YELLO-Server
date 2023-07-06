package com.yello.server.domain.friend.service;

import com.yello.server.domain.friend.dto.FriendsResponse;
import org.springframework.data.domain.Pageable;

public interface FriendService {

    FriendsResponse findAllFriends(Pageable pageable, Long userId);

    void addFriend(Long userId, Long targetId);
}
