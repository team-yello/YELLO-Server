package com.yello.server.domain.purchase.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GoogleSubscriptionGetResponse(
    String productId,
    String expiredAt
) {

    public static GoogleSubscriptionGetResponse of(String productId) {
        return GoogleSubscriptionGetResponse.builder()
            .productId(productId)
            .expiredAt(LocalDateTime.now().toString())
            .build();
    }

}
