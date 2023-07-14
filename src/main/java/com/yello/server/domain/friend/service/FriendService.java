package com.yello.server.domain.friend.service;

import com.yello.server.domain.friend.dto.FriendsResponse;
import com.yello.server.domain.friend.dto.request.KakaoRecommendRequest;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.dto.response.RecommendFriendResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FriendService {

    FriendsResponse findAllFriends(Pageable pageable, Long userId);

    void addFriend(Long userId, Long targetId);

    List<FriendShuffleResponse> shuffleFriend(Long userId);

    List<RecommendFriendResponse> findAllRecommendSchoolFriends(Pageable pageable, Long userId);

    List<RecommendFriendResponse> findAllRecommendKakaoFriends(Pageable pageable, Long userId, KakaoRecommendRequest request);
}
