package com.yello.server.domain.event.dto.request;

import lombok.Builder;

@Builder
public record AdmobRewardRequest(
    String rewardType,
    String randomType,
    String uuid,
    Integer rewardNumber
) {

}
