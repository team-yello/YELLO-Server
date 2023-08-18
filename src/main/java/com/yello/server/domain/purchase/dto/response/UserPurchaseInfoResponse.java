package com.yello.server.domain.purchase.dto.response;

import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserPurchaseInfoResponse(
    Subscribe subscribeState,
    Boolean isSubscribe,
    Integer ticketCount
) {

    public static UserPurchaseInfoResponse of(User user) {
        return UserPurchaseInfoResponse.builder()
            .subscribeState(user.getSubscribe())
            .isSubscribe(user.getSubscribe() != Subscribe.NORMAL)
            .ticketCount(user.getTicketCount())
            .build();
    }

}
