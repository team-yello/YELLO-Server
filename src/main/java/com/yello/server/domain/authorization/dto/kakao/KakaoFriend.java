package com.yello.server.domain.authorization.dto.kakao;

public record KakaoFriend(
        Long id,
        String profile_nickname,
        String profile_thumbnail_image,
        String uuid,
        Boolean favorite,
        Boolean allowed_msg
) { }
