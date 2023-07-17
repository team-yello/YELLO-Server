package com.yello.server.domain.friend.service;

import com.yello.server.domain.friend.dto.FriendsResponse;
import com.yello.server.domain.friend.dto.request.KakaoRecommendRequest;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.dto.response.RecommendFriendResponse;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.user.dto.UserResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.domain.vote.entity.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.yello.server.global.common.ErrorCode.*;
import static com.yello.server.global.common.util.ConstantUtil.RANDOM_COUNT;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    @Override
    public FriendsResponse findAllFriends(Pageable pageable, Long userId) {
        Page<Friend> friendsData = friendRepository.findAllFriendsByUserId(pageable, userId);
        List<UserResponse> friends = friendsData.stream()
                .map(friend -> {
                    User user = friend.getUser();
                    Integer friendCount = friendRepository.findAllByUser(user).size();
                    Integer yelloCount = voteRepository.getCountAllByReceiverUserId(user.getId());
                    return UserResponse.of(user, friendCount, yelloCount);
                })
                .toList();

        return FriendsResponse.of(friendsData.getTotalElements(), friends);
    }

    @Transactional
    @Override
    public void addFriend(Long userId, Long targetId) {
        User target = findUser(targetId);
        User user = findUser(userId);

        Friend friendData = friendRepository.findByFollowingAndFollower(userId, targetId);

        if (friendData != null) {
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
                .filter(recommend -> !user.getId().equals(recommend.getId()))
                .filter(friend -> {
                    return findFriend(userId, friend.getId()) == null;
                })
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

    @Override
    public List<RecommendFriendResponse> findAllRecommendKakaoFriends(Pageable pageable, Long userId, KakaoRecommendRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USERID_NOT_FOUND_USER_EXCEPTION));

        List<String> uuidList = Arrays.stream(request.friendKakaoId())
                .filter(friend -> {
                    Optional<User> userByUuid = userRepository.findByUuid(friend);
                    return friendRepository.findByFollowingAndFollower(userId, userByUuid.orElseThrow(() -> new UserException(USERID_NOT_FOUND_USER_EXCEPTION)).getId()) == null;
                })
                .toList();

        return userRepository.findAllByUuidIn(uuidList, pageable).stream()
                .map(RecommendFriendResponse::of)
                .toList();
    }


    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USERID_NOT_FOUND_USER_EXCEPTION));
    }

    private Friend findFriend(Long userId, Long friendId) {
        return friendRepository.findByFollowingAndFollower(userId, friendId);
    }
}
