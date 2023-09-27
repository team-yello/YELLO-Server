package com.yello.server.domain.purchase.dto.apple;

import com.yello.server.domain.purchase.entity.Purchase;
import lombok.Builder;

@Builder
public record ApplePurchaseVO(
    String transactionId,
    Purchase purchase
) {

    public static ApplePurchaseVO of(String transactionId, Purchase purchase) {
        return ApplePurchaseVO.builder()
            .transactionId(transactionId)
            .purchase(purchase)
            .build();
    }

}
