package com.yello.server.domain.friend.dto.response;

import com.yello.server.domain.friend.entity.Friend;
import lombok.Builder;

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

    @Builder
    public FriendShuffleResponse(Long friendId, String friendName) {
        this.friendId = friendId;
        this.friendName = friendName;
    }
}
