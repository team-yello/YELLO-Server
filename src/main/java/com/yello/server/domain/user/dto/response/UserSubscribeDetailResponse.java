package com.yello.server.domain.user.dto.response;

import static com.yello.server.global.common.factory.TimeFactory.toYearAndMonthFormattedString;

import com.yello.server.domain.purchase.entity.Purchase;
import lombok.Builder;

@Builder
public record UserSubscribeDetailResponse(
    Long id,
    String subscribe,
    String expiredDate
) {

    public static UserSubscribeDetailResponse of(Purchase purchase) {
        return UserSubscribeDetailResponse.builder()
            .id(purchase.getUser().getId())
            .subscribe(purchase.getUser().getSubscribe().getIntial())
            .expiredDate(toYearAndMonthFormattedString(purchase.getUpdatedAt()))
            .build();
    }
}
