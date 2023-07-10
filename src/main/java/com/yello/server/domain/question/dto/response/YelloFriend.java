package com.yello.server.domain.question.dto.response;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.user.entity.User;
import lombok.Builder;

public record YelloFriend(
        Long id,
        String yelloId,
        String name
) {
    public static YelloFriend of(Friend friend) {
        return YelloFriend.builder()
                .id(friend.getTarget().getId())
                .yelloId(friend.getTarget().getYelloId())
                .name(friend.getTarget().getName())
                .build();
    }

    @Builder
    public YelloFriend(Long id, String yelloId, String name) {
        this.id = id;
        this.yelloId = yelloId;
        this.name = name;
    }
}
