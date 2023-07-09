package com.yello.server.domain.user.dto.response;

import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserInfoResponse(
    Long id,
    Long recommendCount,
    String name,
    String yelloId,
    String gender,
    Integer point,
    String social,
    String profileImage,
    String uuid,
    String email,
    String deletedAt,
    String createdAt,
    String updatedAt
) {

    public static UserInfoResponse of(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .recommendCount(user.getRecommendCount())
                .name(user.getName())
                .yelloId(user.getYelloId())
                .gender(user.getGender().toString())
                .point(user.getPoint())
                .social(user.getSocial().toString())
                .profileImage(user.getProfileImage())
                .uuid(user.getUuid())
                .email(user.getEmail())
                .deletedAt(user.getDeletedAt().toString())
                .createdAt(user.getCreatedAt().toString())
                .updatedAt(user.getUpdatedAt().toString())
                .build();
    }
}
