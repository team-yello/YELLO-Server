package com.yello.server.domain.friend.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record SearchFriendResponse(
    Integer totalCount,
    List<SearchFriendVO> friendList
) {

    public static SearchFriendResponse of(Integer totalCount, List<SearchFriendVO> friendList) {
        return SearchFriendResponse.builder()
            .totalCount(totalCount)
            .friendList(friendList)
            .build();
    }

}
