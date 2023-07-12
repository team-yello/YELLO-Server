package com.yello.server.domain.question.dto.response;

import com.yello.server.domain.friend.entity.Friend;
import lombok.Builder;

@Builder
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

}
