package com.yello.server.domain.admin.dto.response;

import static com.yello.server.global.common.factory.TimeFactory.toDateFormattedString;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record AdminCooldownContentVO(
    Long id,
    String name,
    String yelloId,
    String createdAt
) {

    public static AdminCooldownContentVO of(Cooldown cooldown) {
        final User cooldownUser = cooldown.getUser();

        return AdminCooldownContentVO.builder()
            .id(cooldown.getId())
            .name(cooldownUser.getName())
            .yelloId(cooldownUser.getYelloId())
            .createdAt(toDateFormattedString(cooldown.getCreatedAt()))
            .build();
    }
}
