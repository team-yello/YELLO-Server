package com.yello.server.domain.purchase.dto.request;

import lombok.Builder;

@Builder
public record GoogleSubscriptionV2GetRequest(
    String orderId,
    String packageName,
    String productId,
    Long purchaseTime,
    Integer purchaseState,
    String purchaseToken,
    Integer quantity,
    Boolean autoRenewing,
    Boolean acknowledged
) {

}