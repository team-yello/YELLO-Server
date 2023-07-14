package com.yello.server.domain.question.dto.response;

import com.yello.server.domain.friend.entity.Friend;
import lombok.Builder;

@Builder
public record VoteShuffleFriend(
        Long id,
        String yelloId,
        String name
) {
    public static VoteShuffleFriend of(Friend friend) {
        return VoteShuffleFriend.builder()
                .id(friend.getTarget().getId())
                .yelloId(friend.getTarget().getYelloId())
                .name(friend.getTarget().getName())
                .build();
    }

}
