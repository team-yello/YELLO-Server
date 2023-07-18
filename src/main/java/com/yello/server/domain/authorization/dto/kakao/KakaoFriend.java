package com.yello.server.domain.authorization.dto.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoFriend(
        Long id,
        String profile_nickname,
        String profile_thumbnail_image,
        String uuid,
        Boolean favorite,
        Boolean allowed_msg
) { }
