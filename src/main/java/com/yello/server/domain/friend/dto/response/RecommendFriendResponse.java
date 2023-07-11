package com.yello.server.domain.friend.dto.response;

import com.yello.server.domain.user.entity.User;
import lombok.Builder;

public record RecommendFriendResponse(
        Long id,
        String name,
        String schoolData,
        String profileImage
) {
    public static RecommendFriendResponse of(User user) {
        return RecommendFriendResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .schoolData(user.getGroup().getSchoolName() +  " " + user.getGroup().getDepartmentName() + " " +user.getGroup().getAdmissionYear()+ "학번")
                .profileImage(user.getProfileImage())
                .build();
    }

    @Builder
    public RecommendFriendResponse(Long id, String name, String schoolData, String profileImage) {
        this.id = id;
        this.name = name;
        this.schoolData = schoolData;
        this.profileImage = profileImage;
    }
}
