package com.yello.server.domain.purchase.dto.response;

import io.jsonwebtoken.Claims;
import lombok.Builder;

@Builder
public record AppleJwsTransactionResponse(
    String transactionId,
    String originalTransactionId,
    String productId
) {

    public static AppleJwsTransactionResponse of(Claims claims) {
        return AppleJwsTransactionResponse.builder()
            .transactionId(claims.get("transactionId", String.class))
            .originalTransactionId(claims.get("originalTransactionId", String.class))
            .productId(claims.get("productId", String.class))
            .build();
    }

}
