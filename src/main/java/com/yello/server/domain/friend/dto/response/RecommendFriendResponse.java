package com.yello.server.domain.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record RecommendFriendResponse(
    @Schema(description = "해당 유저의 친구들이 받은 총 투표 개수")
    Integer totalCount,
    @Schema(description = "해당 유저의 친구들이 받은 총 투표")
    List<FriendResponse> friends

) {

    public static RecommendFriendResponse of(Integer totalCount, List<FriendResponse> friends) {
        return RecommendFriendResponse.builder()
            .totalCount(totalCount)
            .friends(friends)
            .build();
    }

}
