package com.yello.server.domain.user.dto.request;

import com.yello.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserUpdateRequest(
    String name,
    String yelloId,
    String gender,
    String email,
    String profileImageUrl,
    Long groupId,
    Integer groupAdmissionYear
) {

    public boolean groupInfoEquals(User user) {
        return groupId.equals(user.getGroup().getId()) &&
            groupAdmissionYear.equals(user.getGroupAdmissionYear());
    }
}
