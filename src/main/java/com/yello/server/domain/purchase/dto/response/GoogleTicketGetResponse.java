package com.yello.server.domain.purchase.dto.response;

import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record GoogleTicketGetResponse(
    String productId,
    Integer ticketCount
) {

    public static GoogleTicketGetResponse of(String productId, User user) {
        return GoogleTicketGetResponse.builder()
            .productId(productId)
            .ticketCount(user.getTicketCount())
            .build();
    }
}
