package com.yello.server.domain.friend.service;

import static com.yello.server.global.common.ErrorCode.EXIST_FRIEND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.LACK_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.NOT_FOUND_FRIEND_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.RANDOM_COUNT;

import com.yello.server.domain.friend.dto.request.KakaoRecommendRequest;
import com.yello.server.domain.friend.dto.response.FriendResponse;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.dto.response.FriendsResponse;
import com.yello.server.domain.friend.dto.response.RecommendFriendResponse;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.friend.exception.FriendNotFoundException;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.entity.VoteRepository;
import com.yello.server.global.common.factory.PaginationFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                User targetUser = friend.getTarget();
                Integer friendCount = friendRepository.countAllByUserId(targetUser.getId());
                Integer yelloCount = voteRepository.countAllByReceiverUserId(targetUser.getId());
                return UserResponse.of(targetUser, yelloCount, friendCount);
            })
            .toList();

        return FriendsResponse.of(friendsData.getTotalElements(), friends);
    }

    @Transactional
    @Override
    public void addFriend(Long userId, Long targetId) {
        User target = userRepository.findById(targetId);
        User user = userRepository.findById(userId);

        Optional<Friend> friendData = friendRepository.findByUserAndTarget(userId, targetId);

        if (friendData.isPresent()) {
            throw new FriendException(EXIST_FRIEND_EXCEPTION);
        }

        friendRepository.save(Friend.createFriend(user, target));
        friendRepository.save(Friend.createFriend(target, user));
    }

    @Override
    public List<FriendShuffleResponse> findShuffledFriend(Long userId) {
        User user = userRepository.findById(userId);

        List<Friend> allFriends = friendRepository.findAllByUserId(user.getId());

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
    public RecommendFriendResponse findAllRecommendSchoolFriends(Pageable pageable, Long userId) {
        User user = userRepository.findById(userId);

        List<User> recommendFriends = userRepository.findAllByGroupId(user.getGroup().getId())
            .stream()
            .filter(anotherUser -> !userId.equals(anotherUser.getId()))
            .filter(anotherUser -> friendRepository.findByUserAndTarget(userId, anotherUser.getId()).isEmpty())
            .toList();

        List<FriendResponse> pageList = PaginationFactory.getPage(recommendFriends, pageable)
            .stream()
            .map(FriendResponse::of)
            .toList();

        return RecommendFriendResponse.of(recommendFriends.size(), pageList);
    }
 
    @Transactional
    @Override
    public void deleteFriend(Long userId, Long targetId) {
        User target = userRepository.findById(targetId);
        User user = userRepository.findById(userId);

        findFriend(userId, targetId);
        findFriend(targetId, userId);

        friendRepository.deleteByUserAndTarget(user.getId(), target.getId());
        friendRepository.deleteByUserAndTarget(target.getId(), user.getId());
    }

    @Override
    public RecommendFriendResponse findAllRecommendKakaoFriends(Pageable pageable, Long userId,
        KakaoRecommendRequest request) {
        User user = userRepository.findById(userId);

        List<User> kakaoFriends = Arrays.stream(request.friendKakaoId())
            .map(userRepository::findExistsByUuid)
            .filter(friend -> {
                Optional<Friend> target = friendRepository.findByUserAndTarget(user.getId(), friend.getId());
                return target.isEmpty();
            })
            .toList();

        List<FriendResponse> pageList = PaginationFactory.getPage(kakaoFriends, pageable)
            .stream()
            .map(FriendResponse::of)
            .toList();

        return RecommendFriendResponse.of(kakaoFriends.size(), pageList);
    }

    private Friend findFriend(Long userId, Long targetId) {
        return friendRepository.findByUserAndTarget(userId, targetId)
            .orElseThrow(() -> new FriendNotFoundException(NOT_FOUND_FRIEND_EXCEPTION));
    }
}
