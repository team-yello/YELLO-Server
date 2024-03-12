package com.yello.server.domain.user.dto.request;

import jakarta.annotation.Nullable;

public record UserPostCommentUpdateRequest(
    @Nullable Long id,
    Long postId,
    String userName,
    String yelloId,
    String status,
    String title,
    String subtitle,
    String content
) {

}
