package com.yello.server.domain.authorization.dto.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoFriend(
        Long id,
        String profileNickname,
        String profileThumbnailImage,
        String uuid,
        Boolean favorite,
        Boolean allowedMsg
) { }
