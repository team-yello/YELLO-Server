package com.yello.server.domain.friend.dto;

import com.yello.server.domain.friend.entity.Friend;
import lombok.Builder;

@Builder
public record FriendResponse(
    Long userId,
    String friendName,
    String friendProfileImage,
    String friendGroup
) {

    public static FriendResponse of(Friend friend) {
        return FriendResponse.builder()
            .userId(friend.getId())
            .friendName(friend.getTarget().getName())
            .friendProfileImage(friend.getTarget().getProfileImage())
            .friendGroup(friend.getTarget().getGroup().toString())
            .build();
    }
}
