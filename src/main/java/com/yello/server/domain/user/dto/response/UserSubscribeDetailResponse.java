package com.yello.server.domain.user.dto.response;

import static com.yello.server.global.common.factory.TimeFactory.toYearAndMonthFormattedString;

import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserSubscribeDetailResponse(
    Long id,
    Subscribe subscribe,
    String expiredData
) {
    public static UserSubscribeDetailResponse of(Purchase purchase) {
        return UserSubscribeDetailResponse.builder()
            .id(purchase.getUser().getId())
            .subscribe(purchase.getUser().getSubscribe())
            .expiredData(toYearAndMonthFormattedString(purchase.getUpdatedAt()))
            .build();
    }

}
