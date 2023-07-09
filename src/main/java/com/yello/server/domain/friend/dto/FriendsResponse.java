package com.yello.server.domain.friend.dto;

import com.yello.server.domain.friend.entity.Friend;
import java.util.List;
import lombok.Builder;

@Builder
public record FriendsResponse(
    Integer totalCount,
    List<FriendResponse> friends
) {

    public static FriendsResponse of(List<Friend> friends) {
        return FriendsResponse.builder()
            .totalCount(friends.size())
            .friends(friends.stream()
                .map(FriendResponse::of)
                .toList())
            .build();
    }
}
