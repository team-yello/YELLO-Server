package com.yello.server.domain.friend.service;

import com.yello.server.domain.friend.dto.request.KakaoRecommendRequest;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.dto.response.FriendsResponse;
import com.yello.server.domain.friend.dto.response.RecommendFriendResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface FriendService {

    FriendsResponse findAllFriends(Pageable pageable, Long userId);

    void addFriend(Long userId, Long targetId);

    List<FriendShuffleResponse> shuffleFriend(Long userId);

    RecommendFriendResponse findAllRecommendSchoolFriends(Pageable pageable, Long userId);

    RecommendFriendResponse findAllRecommendKakaoFriends(Pageable pageable, Long userId, KakaoRecommendRequest request);

    void deleteFriend(Long userId, Long targetId);
}
