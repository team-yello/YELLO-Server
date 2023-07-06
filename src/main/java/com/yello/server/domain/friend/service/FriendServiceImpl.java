package com.yello.server.domain.friend.service;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void addFriend(Long userId, Long friendId) {

        User friend = userRepository.findById(friendId).get(); // orElse 처리하기
        User user = userRepository.findById(userId).get();

        Friend friendData = friendRepository.findByFollowingAndFollower(userId, friendId);

        if (friendData != null) {
            return;
        }

        friendRepository.save(Friend.newFriend(friend, user));
    }
}
