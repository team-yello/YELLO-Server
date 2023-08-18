package com.yello.server.domain.purchase.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GoogleSubscriptionV2GetResponse(
    String productId,
    String expiredAt
) {

    public static GoogleSubscriptionV2GetResponse of(String productId) {
        return GoogleSubscriptionV2GetResponse.builder()
            .productId(productId)
            .expiredAt(LocalDateTime.now().toString())
            .build();
    }

}
