package com.yello.server.domain.friend.service;

import static com.yello.server.global.common.ErrorCode.EXIST_FRIEND_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.YELLO_FEMALE;
import static com.yello.server.global.common.util.ConstantUtil.YELLO_MALE;

import com.yello.server.domain.friend.dto.request.KakaoRecommendRequest;
import com.yello.server.domain.friend.dto.response.FriendResponse;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.dto.response.FriendsResponse;
import com.yello.server.domain.friend.dto.response.RecommendFriendResponse;
import com.yello.server.domain.friend.dto.response.SearchFriendResponse;
import com.yello.server.domain.friend.dto.response.SearchFriendVO;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.domain.vote.service.VoteManager;
import com.yello.server.global.common.factory.PaginationFactory;
import java.lang.Character.UnicodeBlock;
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
    private final VoteManager voteManager;

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

        return voteManager.getShuffledFriends(user);
    }

    public RecommendFriendResponse findAllRecommendSchoolFriends(Pageable pageable, Long userId) {
        final User user = userRepository.getById(userId);

        List<User> recommendFriends =
            userRepository.findAllByGroupId(user.getGroup().getId())
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

        Friend friendByUser = friendRepository.getByUserAndTarget(userId, targetId);
        Friend friendByTarget = friendRepository.getByUserAndTarget(targetId, userId);

        friendRepository.delete(friendByUser);
        friendRepository.delete(friendByTarget);
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

    public SearchFriendResponse searchFriend(Long userId, Pageable pageable,
        String keyword) {
        final User user = userRepository.getById(userId);
        final String groupName = user.getGroup().getGroupName();
        List<String> uuidList = Arrays.asList(YELLO_FEMALE, YELLO_MALE);

        List<User> friendList = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return SearchFriendResponse.of(0, Collections.emptyList());
        }

        if (!isEnglish(keyword)) {
            friendList.addAll(
                userRepository.findAllByGroupContainingName(groupName, keyword, uuidList));
            friendList.addAll(
                userRepository.findAllByOtherGroupContainingName(groupName, keyword, uuidList));

        } else {
            friendList.addAll(
                userRepository.findAllByGroupContainingYelloId(groupName, keyword, uuidList));
            friendList.addAll(
                userRepository.findAllByOtherGroupContainingYelloId(groupName, keyword, uuidList));
        }

        List<SearchFriendVO> pageList = PaginationFactory.getPage(friendList, pageable)
            .stream()
            .filter(friend -> !userId.equals(friend.getId()))
            .map(friend -> SearchFriendVO.of(friend,
                friendRepository.existsByUserAndTarget(userId, friend.getId())))
            .toList();

        return SearchFriendResponse.of(friendList.size(), pageList);
    }

    public boolean isEnglish(String keyword) {
        for (char c : keyword.toCharArray()) {
            if (Character.UnicodeBlock.of(c) != UnicodeBlock.BASIC_LATIN) {
                return false;
            }
        }
        return true;
    }
}
