package com.yello.server.domain.friend.service;

import static com.yello.server.global.common.ErrorCode.EXIST_FRIEND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.LACK_USER_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.RANDOM_COUNT;

import com.yello.server.domain.friend.dto.request.KakaoRecommendRequest;
import com.yello.server.domain.friend.dto.response.FriendResponse;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.dto.response.FriendsResponse;
import com.yello.server.domain.friend.dto.response.RecommendFriendResponse;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.global.common.factory.PaginationFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public FriendsResponse findAllFriends(Pageable pageable, Long userId) {
        final Page<Friend> friendsData = friendRepository.findAllFriendsByUserId(pageable, userId);
        List<UserResponse> friends = friendsData.stream()
            .map(friend -> {
                User targetUser = friend.getTarget();
                Integer friendCount = friendRepository.countAllByUserId(targetUser.getId());
                Integer yelloCount = voteRepository.countAllByReceiverUserId(targetUser.getId());
                return UserResponse.of(targetUser, yelloCount, friendCount);
            })
            .toList();

        return FriendsResponse.of(friendsData.getTotalElements(), friends);
    }

    @Transactional
    public void addFriend(Long userId, Long targetId) {
        final User target = userRepository.findById(targetId);
        final User user = userRepository.findById(userId);

        final Friend friendData = friendRepository.findByUserAndTarget(userId, targetId);

        if (ObjectUtils.isNotEmpty(friendData)) {
            throw new FriendException(EXIST_FRIEND_EXCEPTION);
        }

        friendRepository.save(Friend.createFriend(user, target));
        friendRepository.save(Friend.createFriend(target, user));
    }

    public List<FriendShuffleResponse> findShuffledFriend(Long userId) {
        final User user = userRepository.findById(userId);

        final List<Friend> allFriends = friendRepository.findAllByUserId(user.getId());

        if (allFriends.size() < RANDOM_COUNT) {
            throw new FriendException(LACK_USER_EXCEPTION);
        }

        Collections.shuffle(allFriends);

        return allFriends.stream()
            .map(FriendShuffleResponse::of)
            .limit(RANDOM_COUNT)
            .toList();
    }

    public RecommendFriendResponse findAllRecommendSchoolFriends(Pageable pageable, Long userId) {
        final User user = userRepository.findById(userId);

        List<User> recommendFriends = userRepository.findAllByGroupId(user.getGroup().getId())
            .stream()
            .filter(target -> !userId.equals(target.getId()))
            .filter(target -> !friendRepository.existsByUserAndTarget(userId, target.getId()))
            .toList();

        List<FriendResponse> pageList = PaginationFactory.getPage(recommendFriends, pageable)
            .stream()
            .map(FriendResponse::of)
            .toList();

        return RecommendFriendResponse.of(recommendFriends.size(), pageList);
    }

    @Transactional
    public void deleteFriend(Long userId, Long targetId) {
        final User target = userRepository.findById(targetId);
        final User user = userRepository.findById(userId);

        friendRepository.findByUserAndTarget(userId, targetId);
        friendRepository.findByUserAndTarget(targetId, userId);

        friendRepository.deleteByUserAndTarget(user.getId(), target.getId());
    }

    public RecommendFriendResponse findAllRecommendKakaoFriends(Pageable pageable, Long userId,
        KakaoRecommendRequest request) {
        final User user = userRepository.findById(userId);

        List<User> kakaoFriends = Arrays.stream(request.friendKakaoId())
            .filter(userRepository::existsByUuid)
            .map(userRepository::findByUuid)
            .filter(friend -> !friendRepository.existsByUserAndTarget(user.getId(), friend.getId()))
            .toList();

        List<FriendResponse> pageList = PaginationFactory.getPage(kakaoFriends, pageable)
            .stream()
            .map(FriendResponse::of)
            .toList();

        return RecommendFriendResponse.of(kakaoFriends.size(), pageList);
    }
}
