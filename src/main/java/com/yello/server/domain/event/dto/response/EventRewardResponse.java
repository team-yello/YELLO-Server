package com.yello.server.domain.event.dto.response;

import lombok.Builder;

@Builder
public record EventRewardResponse(
    String rewardTag,
    Long rewardValue,
    String rewardTitle,
    String rewardImage
) {

}
