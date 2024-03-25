package com.yello.server.domain.statistics.dto;

import lombok.Builder;

@Builder
public record SignUpVO(
    Long count,
    Long maleCount,
    Long femaleCount,
    Long deleteAtCount
) {

}
