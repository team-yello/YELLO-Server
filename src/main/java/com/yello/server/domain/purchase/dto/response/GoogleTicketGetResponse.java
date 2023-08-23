package com.yello.server.domain.purchase.dto.response;

import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record GoogleTicketV1GetResponse(
    String productId,
    Integer ticketCount
) {

    public static GoogleTicketV1GetResponse of(String productId, User user) {
        return GoogleTicketV1GetResponse.builder()
            .productId(productId)
            .ticketCount(user.getTicketCount())
            .build();
    }
}
