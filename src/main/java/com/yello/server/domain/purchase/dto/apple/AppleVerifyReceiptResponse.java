package com.yello.server.domain.purchase.dto.apple;

import com.fasterxml.jackson.annotation.JsonAlias;

public record AppleVerifyReceiptResponse(
    String environment,
    @JsonAlias("is-retryable")
    boolean isRetryable,
    @JsonAlias("latest_receipt")
    String latestReceipt,
    int status,
    ReceiptVO receipt
) {

}
