package com.yello.server.domain.user.dto.request;

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

}
