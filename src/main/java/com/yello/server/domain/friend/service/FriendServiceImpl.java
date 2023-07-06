package com.yello.server.domain.friend.service;

import com.yello.server.domain.friend.dto.FriendsResponse;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Override
    public FriendsResponse findAllFriends(Pageable pageable, Long userId) {
        List<Friend> friends = friendRepository.findAllFriendsByUserId(pageable, userId)
            .stream()
            .toList();

        return FriendsResponse.of(friends);
    }

    @Transactional
    @Override
    public void addFriend(Long userId, Long targetId) {

        User target = userRepository.findById(targetId).get(); // orElse 처리하기
        User user = userRepository.findById(userId).get();

        Friend friendData = friendRepository.findByFollowingAndFollower(userId, targetId);

        if (friendData!=null) {
            return;
        }

        friendRepository.save(Friend.createFriend(user, target));
        friendRepository.save(Friend.createFriend(target, user));
    }
}
