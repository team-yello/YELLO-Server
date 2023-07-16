package com.yello.server.domain.authorization.dto.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoFriendsInfo(
        List<KakaoFriend> elements,
        Integer totalCount,
        Integer favoriteCount,
        String beforeUrl,
        String afterUrl,
        String resultId
) { }
