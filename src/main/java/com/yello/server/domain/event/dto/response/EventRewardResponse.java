package com.yello.server.domain.event.dto.response;

import com.yello.server.domain.event.entity.EventInstanceReward;
import lombok.Builder;

@Builder
public record EventRewardResponse(
    String rewardTag,
    Long rewardValue,
    String rewardTitle,
    String rewardImage
) {

    public static EventRewardResponse of(EventInstanceReward eventInstanceReward) {
        return EventRewardResponse.builder()
            .rewardTag(eventInstanceReward.getRewardTag())
            .rewardValue(eventInstanceReward.getRewardValue())
            .rewardTitle(eventInstanceReward.getRewardTitle())
            .rewardImage(eventInstanceReward.getRewardImage())
            .build();
    }
}
