package com.yello.server.domain.friend.service;

import static com.yello.server.global.common.ErrorCode.EXIST_FRIEND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.LACK_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.USERID_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.RANDOM_COUNT;

import com.yello.server.domain.friend.dto.FriendsResponse;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.dto.response.RecommendFriendResponse;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserException;
import java.util.Collections;
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
        User target = findUser(targetId);
        User user = findUser(userId);

        Friend friendData = friendRepository.findByFollowingAndFollower(userId, targetId);

        if (friendData!=null) {
            throw new FriendException(EXIST_FRIEND_EXCEPTION);
        }

        friendRepository.save(Friend.createFriend(user, target));
        friendRepository.save(Friend.createFriend(target, user));
    }

    @Override
    public List<FriendShuffleResponse> shuffleFriend(Long userId) {
        User user = findUser(userId);

        List<Friend> allFriends = friendRepository.findAllByUser(user);

        if (allFriends.size() < RANDOM_COUNT) {
            throw new FriendException(LACK_USER_EXCEPTION);
        }

        Collections.shuffle(allFriends);

        return allFriends.stream()
            .map(FriendShuffleResponse::of)
            .limit(RANDOM_COUNT)
            .toList();
    }

    @Override
    public List<RecommendFriendResponse> findAllRecommendSchoolFriends(Pageable pageable, Long userId) {
        User user = findUser(userId);

        List<User> recommendFriends = userRepository.findAllByGroupId(user.getGroup().getId(), pageable)
            .stream()
            .toList();

        return recommendFriends.stream()
            .map(RecommendFriendResponse::of)
            .toList();
    }

    @Transactional
    @Override
    public void deleteFriend(Long userId, Long targetId) {
        User target = findUser(targetId);
        User user = findUser(userId);

        friendRepository.deleteByFollowingAndFollower(user.getId(), target.getId());
        friendRepository.deleteByFollowingAndFollower(target.getId(), user.getId());
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserException(USERID_NOT_FOUND_USER_EXCEPTION));
    }
}
