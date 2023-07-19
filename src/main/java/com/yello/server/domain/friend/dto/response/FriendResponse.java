package com.yello.server.domain.friend.dto.response;

import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record FriendResponse(
        Long id,
        String name,
        String group,
        String profileImage
) {

    public static FriendResponse of(User user) {
        return FriendResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .group(user.getGroupString())
                .profileImage(user.getProfileImage())
                .build();
    }
}
