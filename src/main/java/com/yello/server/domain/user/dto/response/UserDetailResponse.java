package com.yello.server.domain.user.dto.response;

import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserDetailResponse(
    Long userId,
    String name,
    String profileImageUrl,
    String group,
    String yelloId,
    Integer yelloCount,
    Integer friendCount,
    Integer point
) {
 
    public static UserDetailResponse of(User user, Integer yelloCount, Integer friendCount) {
        return UserDetailResponse.builder()
            .userId(user.getId())
            .name(user.getName())
            .group(user.getGroupString())
            .profileImageUrl(user.getProfileImage())
            .yelloId(user.getYelloId())
            .yelloCount(yelloCount)
            .friendCount(friendCount)
            .point(user.getPoint())
            .build();
    }
}
