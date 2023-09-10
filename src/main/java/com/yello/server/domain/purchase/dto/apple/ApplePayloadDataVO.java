package com.yello.server.domain.purchase.dto.apple;

public record ApplePayloadDataVO(
    String appAppleId,
    String bundleId,
    String environment,
    String signedTransactionInfo,
    String signedRenewalInfo,
    int status

) {

}
