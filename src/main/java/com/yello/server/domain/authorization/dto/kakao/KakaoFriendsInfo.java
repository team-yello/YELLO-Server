package com.yello.server.domain.authorization.dto.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoFriendsInfo(
        List<KakaoFriend> elements,
        Integer total_count,
        Integer favorite_count,
        String before_url,
        String after_url,
        String result_id
) { }
