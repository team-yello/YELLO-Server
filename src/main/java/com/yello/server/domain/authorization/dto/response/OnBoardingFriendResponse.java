package com.yello.server.domain.authorization.dto.response;

import com.yello.server.domain.user.entity.User;
import java.util.List;
import lombok.Builder;

@Builder
public record OnBoardingFriendResponse(
    Integer totalCount,
    List<OnBoardingFriend> friendList
) {

    public static OnBoardingFriendResponse of(int totalCount, List<User> users) {
        List<OnBoardingFriend> onBoardingFriends = users.stream()
            .map(OnBoardingFriend::of)
            .toList();

        return OnBoardingFriendResponse.builder()
            .totalCount(totalCount)
            .friendList(onBoardingFriends)
            .build();
    }
}
