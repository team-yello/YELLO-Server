package com.yello.server.domain.friend.dto.response;

import com.yello.server.domain.friend.entity.Friend;
import lombok.Builder;


@Builder
public record FriendShuffleResponse(
        Long friendId,
        String friendName
) {

    public static FriendShuffleResponse of(Friend friend) {
        return FriendShuffleResponse.builder()
                .friendId(friend.getTarget().getId())
                .friendName(friend.getTarget().getName())
                .build();
    }

}
