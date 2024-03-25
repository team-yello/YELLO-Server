package com.yello.server.domain.statistics.dto;

import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import java.util.List;
import lombok.Builder;

@Builder
public record RevenueVO(
    List<RevenueItem> revenueItemList
) {

    @Builder
    public record RevenueItem(
        Gateway gateway,
        ProductType productType,
        Long purchaseCount,
        Long totalPrice
    ) {

    }
}
