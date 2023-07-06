package com.yello.server.domain.friend.dto.response;

import lombok.Builder;
import lombok.Data;

public record FriendShuffleResponse(
        Long friendId,
        String friendName
) {

    @Builder
    public FriendShuffleResponse(Long friendId, String friendName) {
        this.friendId = friendId;
        this.friendName = friendName;
    }
}
