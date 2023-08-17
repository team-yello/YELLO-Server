package com.yello.server.domain.friend.service;

import com.yello.server.domain.user.entity.User;
import java.util.List;

public interface FriendManager {

    List<User> getRecommendedFriendsForOnBoarding(List<String> friendKakaoIds);

}
