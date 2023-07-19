package com.yello.server.domain.friend.dto;

import com.yello.server.domain.user.dto.response.UserResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record FriendsResponse(
    Long totalCount,
    List<UserResponse> friends
) {

    public static FriendsResponse of(Long totalCount, List<UserResponse> friends) {
        return FriendsResponse.builder()
            .totalCount(totalCount)
            .friends(friends)
            .build();
    }
}
