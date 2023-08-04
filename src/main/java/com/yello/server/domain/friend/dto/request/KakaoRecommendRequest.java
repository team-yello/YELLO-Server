package com.yello.server.domain.friend.dto.request;

import lombok.Builder;

@Builder
public record KakaoRecommendRequest(
    String[] friendKakaoId
) {

}
