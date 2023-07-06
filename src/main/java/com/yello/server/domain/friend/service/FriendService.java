package com.yello.server.domain.friend.service;

import com.yello.server.domain.friend.dto.FriendsResponse;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FriendService {

    FriendsResponse findAllFriends(Pageable pageable, Long userId);

    void addFriend(Long userId, Long targetId);

    List<FriendShuffleResponse> shuffleFriend(Long userId);
}
