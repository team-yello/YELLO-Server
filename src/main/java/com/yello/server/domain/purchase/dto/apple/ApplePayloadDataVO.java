package com.yello.server.domain.purchase.dto.apple;

import lombok.Builder;

@Builder
public record ApplePayloadDataVO(
    String appAppleId,
    String bundleId,
    String bundleVersion,
    String environment,
    String signedTransactionInfo,
    String signedRenewalInfo,
    int status

) {

}
