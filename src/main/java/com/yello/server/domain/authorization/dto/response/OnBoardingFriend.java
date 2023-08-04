package com.yello.server.domain.authorization.dto.response;

import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record OnBoardingFriend(
    String group,
    Long id,
    String name,
    String profileImage,
    String groupName
) {

    public static OnBoardingFriend of(User user) {
        return OnBoardingFriend.builder()
            .group(user.getSocial() == Social.KAKAO ? "KAKAO" : "SCHOOL")
            .id(user.getId())
            .name(user.getName())
            .profileImage(user.getProfileImage())
            .groupName(user.getGroup().toString())
            .build();
    }
}


