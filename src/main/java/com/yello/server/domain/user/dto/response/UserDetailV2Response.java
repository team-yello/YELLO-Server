package com.yello.server.domain.user.dto.response;

import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserDetailV2Response(
    /* Default */
    Long userId,
    String name,
    String yelloId,
    String gender,
    String email,
    String profileImageUrl,
    /* Device */
    String social,
    String uuid,
    String deviceToken,
    /* Group */
    Long groupId,
    String group,
    String groupType,
    String groupName,
    String subGroupName,
    Integer groupAdmissionYear,
    /* Domain */
    Long recommendCount,
    Integer ticketCount,
    Integer point,
    String subscribe,
    Integer yelloCount,
    Integer friendCount
) {

    public static UserDetailV2Response of(User user, UserGroup userGroup, Integer yelloCount, Integer friendCount) {
        return UserDetailV2Response.builder()
            .userId(user.getId())
            .name(user.getName())
            .yelloId(user.getYelloId())
            .gender(user.getGender().getInitial())
            .email(user.getEmail())
            .profileImageUrl(user.getProfileImage())
            .social(user.getSocial().toString())
            .uuid(user.getUuid())
            .deviceToken(user.getDeviceToken())
            .groupId(userGroup.getId())
            .group(user.toGroupString())
            .groupType(userGroup.getUserGroupType().getInitial())
            .groupName(userGroup.getGroupName())
            .subGroupName(userGroup.getSubGroupName())
            .groupAdmissionYear(user.getGroupAdmissionYear())
            .recommendCount(user.getRecommendCount())
            .ticketCount(user.getTicketCount())
            .point(user.getPoint())
            .subscribe(user.getSubscribe().getInitial())
            .yelloCount(yelloCount)
            .friendCount(friendCount)
            .build();
    }
}
