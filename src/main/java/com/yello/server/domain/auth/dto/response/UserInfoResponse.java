package com.yello.server.domain.auth.dto.response;

import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserInfoResponse(
    Long id,
    Long recommendCount,
    String name,
    String yelloId,
    Gender gender,
    Integer point,
    Social social,
    String profileImage,
    String uuid,
    String email,
    LocalDateTime createdAt
) {

    public static UserInfoResponse of(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .recommendCount(user.getRecommendCount())
                .name(user.getName())
                .yelloId(user.getYelloId())
                .gender(user.getGender())
                .point(user.getPoint())
                .social(user.getSocial())
                .profileImage(user.getProfileImage())
                .uuid(user.getUuid())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
