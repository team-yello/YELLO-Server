package com.yello.server.domain.admin.dto.response;

import static com.yello.server.global.common.factory.TimeFactory.toDateFormattedString;

import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record AdminUserContentVO(
    Long id,
    String name,
    String yelloId,
    String group,
    String imageUrl,
    String createdAt,
    String deletedAt
) {

    public static AdminUserContentVO of(User user) {
        final School userGroup = user.getGroup();

        return AdminUserContentVO.builder()
            .id(user.getId())
            .name(user.getName())
            .yelloId(user.getYelloId())
            .group(userGroup == null ? "" : userGroup.toString())
            .imageUrl(user.getProfileImage())
            .createdAt(toDateFormattedString(user.getCreatedAt()))
            .deletedAt(toDateFormattedString(user.getDeletedAt()))
            .build();
    }
}
