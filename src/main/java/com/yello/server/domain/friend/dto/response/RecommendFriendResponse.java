package com.yello.server.domain.friend.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record RecommendFriendResponse(
    Integer totalCount,
    List<FriendResponse> friends

) {

    public static RecommendFriendResponse of(Integer totalCount, List<FriendResponse> friends) {
        return RecommendFriendResponse.builder()
            .totalCount(totalCount)
            .friends(friends)
            .build();
    }

}
