package com.yello.server.domain.purchase.dto.response;

import lombok.Builder;

@Builder
public record AppleJwsTransactionResponse(
    String transactionId,
    String originalTransactionId
) {

    public static AppleJwsTransactionResponse of(String originalTransactionId,
        String transactionId) {
        return AppleJwsTransactionResponse.builder()
            .transactionId(transactionId)
            .originalTransactionId(originalTransactionId)
            .build();
    }
}
