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
import com.yello.server.infrastructure.firebase.service.NotificationService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Builder
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final NotificationService notificationService;

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
    public Friend addFriend(Long userId, Long targetId) {
        final User target = userRepository.getById(targetId);
        final User user = userRepository.getById(userId);

        final Optional<Friend> friendData = friendRepository.findByUserAndTarget(userId, targetId);

        if (friendData.isPresent()) {
            throw new FriendException(EXIST_FRIEND_EXCEPTION);
        }

        Friend friend = friendRepository.save(Friend.createFriend(user, target));
        friendRepository.save(Friend.createFriend(target, user));
        return friend;
    }

    public List<FriendShuffleResponse> findShuffledFriend(Long userId) {
        final User user = userRepository.getById(userId);

        final List<Friend> allFriends = new ArrayList<>(friendRepository.findAllByUserId(user.getId()));

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
        final User user = userRepository.getById(userId);

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
        final User target = userRepository.getById(targetId);
        final User user = userRepository.getById(userId);

        friendRepository.getByUserAndTarget(userId, targetId).delete();
        friendRepository.getByUserAndTarget(targetId, userId).delete();
    }

    public RecommendFriendResponse findAllRecommendKakaoFriends(Pageable pageable, Long userId,
        KakaoRecommendRequest request) {
        final User user = userRepository.getById(userId);

        List<User> kakaoFriends = Arrays.stream(request.friendKakaoId())
            .filter(userRepository::existsByUuid)
            .map(userRepository::getByUuid)
            .filter(friend -> !friendRepository.existsByUserAndTarget(user.getId(), friend.getId()))
            .toList();

        List<FriendResponse> pageList = PaginationFactory.getPage(kakaoFriends, pageable)
            .stream()
            .map(FriendResponse::of)
            .toList();

        return RecommendFriendResponse.of(kakaoFriends.size(), pageList);
    }
}
