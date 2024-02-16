package com.yello.server.domain.event.dto.response;

import com.yello.server.domain.user.entity.UserData;
import com.yello.server.global.common.factory.TimeFactory;
import lombok.Builder;

@Builder
public record GetIsPossibleAdmob(
    String createdAt,
    Boolean isPossible
) {
    public static GetIsPossibleAdmob of(UserData userAdmob) {
        return GetIsPossibleAdmob.builder()
            .createdAt(userAdmob.getValue())
            .isPossible(userAdmob.isPossible())
            .build();
    }

}
