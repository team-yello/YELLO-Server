package com.yello.server.domain.friend.dto.response;

import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record SearchFriendVO(
    Long id,
    String name,
    String group,
    String profileImage,
    String yelloId,
    Boolean isFriend
) {

    public static SearchFriendVO of(User user, Boolean isFriend) {
        return SearchFriendVO.builder()
            .id(user.getId())
            .name(user.getName())
            .group(user.toGroupString())
            .profileImage(user.getProfileImage())
            .yelloId(user.getYelloId())
            .isFriend(isFriend)
            .build();
    }
}
