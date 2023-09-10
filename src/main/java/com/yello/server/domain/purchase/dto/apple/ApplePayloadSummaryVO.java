package com.yello.server.domain.purchase.dto.apple;

public record ApplePayloadSummaryVO(
    String requestIdentifier,
    String environment,
    String appAppleId,
    String bundleId,
    String productId
) {

}
