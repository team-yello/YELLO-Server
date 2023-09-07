package com.yello.server.domain.admin.dto.response;

import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record AdminUserDetailResponse(
    Long id,
    Long recommendCount,
    String name,
    String yelloId,
    String gender,
    Integer point,
    String social,
    String profileImage,
    String uuid,
    String deletedAt,
    String group,
    int groupAdmissionYear,
    String email,
    Integer ticketCount,
    String deviceToken,
    String subscribe
) {

    public static AdminUserDetailResponse of(User user) {
        return AdminUserDetailResponse.builder()
            .id(user.getId())
            .recommendCount(user.getRecommendCount())
            .name(user.getName())
            .yelloId(user.getYelloId())
            .gender(user.getGender().toString())
            .point(user.getPoint())
            .social(user.getSocial().toString())
            .profileImage(user.getProfileImage())
            .uuid(user.getUuid())
            .deletedAt(user.getDeletedAt().toString())
            .group(user.getGroup().toString())
            .groupAdmissionYear(user.getGroupAdmissionYear())
            .email(user.getEmail())
            .ticketCount(user.getTicketCount())
            .deviceToken(user.getDeviceToken())
            .subscribe(user.getSubscribe().toString())
            .build();
    }
}
