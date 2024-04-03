package com.yello.server.domain.user.dto.response;

import static com.yello.server.global.common.factory.TimeFactory.toYearAndMonthFormattedString;
import static com.yello.server.global.common.util.ConstantUtil.SUBSCRIBE_DAYS;

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
            .subscribe(purchase.getUser().getSubscribe().getInitial())
            .expiredDate(
                toYearAndMonthFormattedString(purchase.getUpdatedAt() !=null ? purchase.getUpdatedAt().plusDays(SUBSCRIBE_DAYS):null))
            .build();
    }
}
