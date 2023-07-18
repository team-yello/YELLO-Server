package com.yello.server.domain.authorization.dto.response;

import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record OnBoardingFriendResponse(
        Integer totalCount,
        List<OnBoardingFriend> friendList
) {
    @Builder
    private record OnBoardingFriend(
            String group,
            Long id,
            String name,
            String profileImage,
            String groupName
    ) {}

    public static OnBoardingFriendResponse of(List<User> users) {
        List<OnBoardingFriend> result = users.stream().map(user -> OnBoardingFriend.builder()
                        .group(user.getSocial() == Social.KAKAO ? "KAKAO" : "SCHOOL")
                        .id(user.getId())
                        .name(user.getName())
                        .profileImage(user.getProfileImage())
                        .groupName(user.getGroup().toString())
                        .build())
                .toList();

        return OnBoardingFriendResponse.builder()
                .totalCount(result.size())
                .friendList(result)
                .build();
    }
}
