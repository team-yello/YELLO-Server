package com.yello.server.domain.purchase.dto.apple;

import lombok.Builder;

@Builder
public record AppleTransaction(
    String transactionId,
    String productId
) {

}
