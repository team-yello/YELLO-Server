package com.yello.server.domain.authorization.dto.response;

import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record OnBoardingFriendResponse(
    String group,
    Long id,
    String name,
    String profileImage,
    String groupName
) {
    public static List<OnBoardingFriendResponse> listOf(List<User> users) {
        List<OnBoardingFriendResponse> result = new ArrayList<>();

        users.forEach(user -> {
                    result.add(OnBoardingFriendResponse.builder()
                            .group(user.getSocial() == Social.KAKAO ? "KAKAO" : "SCHOOL")
                                    .id(user.getId())
                                    .name(user.getName())
                                    .profileImage(user.getProfileImage())
                                    .groupName(user.getGroup().toString())
                            .build());
                });

        return result;
    }
}
