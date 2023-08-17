package com.yello.server.domain.friend.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoFriendResponse(
    List<FriendVO> elements,
    Integer total_count
) {

}
