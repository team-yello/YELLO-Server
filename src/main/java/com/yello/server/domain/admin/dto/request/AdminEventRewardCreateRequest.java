package com.yello.server.domain.admin.dto.request;

public record AdminEventRewardCreateRequest(
    String tag,
    Long maxRewardValue,
    Long minRewardValue,
    String title,
    String image
) {

}
