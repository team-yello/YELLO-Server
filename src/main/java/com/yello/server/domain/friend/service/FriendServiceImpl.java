package com.yello.server.domain.friend.service;

import com.yello.server.domain.friend.dto.FriendsResponse;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.yello.server.global.common.util.ConstantUtil.RANDOM_COUNT;

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

        User target = userRepository.findById(targetId).orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));

        Friend friendData = friendRepository.findByFollowingAndFollower(userId, targetId);

        if (friendData != null) {
            throw new FriendException(ErrorCode.EXIST_FRIEND_EXCEPTION, ErrorCode.EXIST_FRIEND_EXCEPTION.getMessage());
        }

        friendRepository.save(Friend.createFriend(user, target));
        friendRepository.save(Friend.createFriend(target, user));
    }

    @Override
    public List<FriendShuffleResponse> shuffleFriend(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        ErrorCode.NOT_FOUND_USER_EXCEPTION,
                        ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()
                ));

        List<Friend> allFriends = friendRepository.findAllByUser(user);

        if (allFriends.size() < RANDOM_COUNT) {
            throw new FriendException(ErrorCode.FRIEND_COUNT_LACK_EXCEPTION, ErrorCode.FRIEND_COUNT_LACK_EXCEPTION.getMessage());
        }

        Collections.shuffle(allFriends);

        return allFriends.stream()
                .map(FriendShuffleResponse::of)
                .limit(RANDOM_COUNT)
                .collect(Collectors.toList());
    }
}
