package com.yello.server.domain.friend.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record RecommendFriendResponse(
        int totalCount,
        List<FriendResponse> friends

) {
    public static RecommendFriendResponse of(int totalCount, List<FriendResponse> friends) {
        return RecommendFriendResponse.builder()
                .totalCount(totalCount)
                .friends(friends)
                .build();
    }

}
