package com.yello.server.domain.friend.dto.response;

public record SearchFriendVO(
    Long id,
    String name,
    String group,
    String profileImage,
    String yelloId
) {

}
