package com.yello.server.domain.purchase.dto.apple;

import lombok.Builder;

@Builder
public record AppleOrderResponse(
    int appAppleId,
    String environment
) {

}
