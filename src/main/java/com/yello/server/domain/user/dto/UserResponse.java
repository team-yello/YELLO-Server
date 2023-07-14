package com.yello.server.domain.user.dto;

import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserResponse(
    Long userId,
    String name,
    String group,
    String yelloId,
    Integer yelloCount,
    Integer friendCount,
    Integer point
) {

    public static UserResponse of(User user, Integer yelloCount, Integer friendCount) {
        return UserResponse.builder()
            .userId(user.getId())
            .name(user.getName())
            .group(user.getGroup().toString())
            .yelloId(user.getYelloId())
            .yelloCount(yelloCount)
            .friendCount(friendCount)
            .point(user.getPoint())
            .build();
    }
}
