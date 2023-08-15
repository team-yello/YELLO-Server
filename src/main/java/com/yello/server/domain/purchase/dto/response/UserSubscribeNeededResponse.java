package com.yello.server.domain.purchase.dto.response;

import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserSubscribeNeededResponse(
    Subscribe subscribe,
    Boolean isSubscribeNeeded
) {

    public static UserSubscribeNeededResponse of(User user, Boolean isSubscribeNeeded) {
        return UserSubscribeNeededResponse.builder()
            .subscribe(user.getSubscribe())
            .isSubscribeNeeded(isSubscribeNeeded)
            .build();
    }
}
