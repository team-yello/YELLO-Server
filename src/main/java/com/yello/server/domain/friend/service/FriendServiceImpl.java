package com.yello.server.domain.friend.service;

import static com.yello.server.global.common.ErrorCode.EXIST_FRIEND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.LACK_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.USERID_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.RANDOM_COUNT;

import com.yello.server.domain.friend.dto.FriendsResponse;
import com.yello.server.domain.friend.dto.request.KakaoRecommendRequest;
import com.yello.server.domain.friend.dto.response.FriendResponse;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.dto.response.RecommendFriendResponse;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.domain.vote.entity.VoteRepository;
import com.yello.server.global.common.util.ListUtil;
import com.yello.server.global.common.util.PaginationUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
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
          User user = friend.getTarget();
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

    Optional<Friend> friendData = friendRepository.findByUserAndTarget(userId, targetId);

    if (friendData.isEmpty()) {
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
  public RecommendFriendResponse findAllRecommendSchoolFriends(Pageable pageable, Long userId) {
    User user = findUser(userId);

    List<User> recommendFriends = userRepository.findAllByGroupId(user.getGroup().getId())
        .stream()
        .filter(recommend -> !user.getId().equals(recommend.getId()))
        .filter(friend -> findFriend(userId, friend.getId()).isEmpty())
        .toList();

    val pageList = PaginationUtil.getPage(recommendFriends, pageable).stream()
        .map(FriendResponse::of)
        .toList();

    return RecommendFriendResponse.of(recommendFriends.size(), pageList);
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
  public RecommendFriendResponse findAllRecommendKakaoFriends(Pageable pageable, Long userId,
                                                              KakaoRecommendRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserException(USERID_NOT_FOUND_USER_EXCEPTION));

    val kakaoFriends = Arrays.stream(request.friendKakaoId())
        .map(userRepository::findByUuid)
        .filter(Optional::isPresent)
        .filter(friend -> {
          Optional<Friend> target = friendRepository.findByUserAndTarget(user.getId(), friend.get().getId());
          return target.isEmpty();
        })
        .toList();

    val pageList = PaginationUtil.getPage(ListUtil.toList(kakaoFriends), pageable).stream()
        .map(FriendResponse::of)
        .toList();

    return RecommendFriendResponse.of(kakaoFriends.size(), pageList);
  }


  private User findUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new UserException(USERID_NOT_FOUND_USER_EXCEPTION));
  }

  private Optional<Friend> findFriend(Long userId, Long friendId) {
    return friendRepository.findByUserAndTarget(userId, friendId);
  }
}
