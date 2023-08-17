package com.yello.server.domain.friend.service;

import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.global.common.factory.ListFactory;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendManagerImpl implements FriendManager {

    private final UserRepository userRepository;

    @Override
    public List<User> getRecommendedFriendsForOnBoarding(List<String> friendKakaoIds) {
        List<User> users = ListFactory.toNonNullableList(friendKakaoIds.stream()
            .map(String::valueOf)
            .map(userRepository::findByUuid)
            .toList());

        return users.stream()
            .distinct()
            .sorted(Comparator.comparing(User::getName))
            .toList();
    }
}
