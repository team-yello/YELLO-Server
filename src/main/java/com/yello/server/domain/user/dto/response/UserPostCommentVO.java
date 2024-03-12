package com.yello.server.domain.user.dto.response;

import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserPost;
import com.yello.server.domain.user.entity.UserPostComment;
import lombok.Builder;

@Builder
public record UserPostCommentVO(
    Long id,
    Long userPostId,
    Long userId,
    String status,
    String userName,
    String yelloId,
    String title,
    String subtitle,
    String content,
    String createdAt,
    String updatedAt
) {

    public static UserPostCommentVO of(UserPostComment userPostComment) {
        UserPost userPost = userPostComment.getUserPost();
        User user = userPostComment.getUser();
        return UserPostCommentVO.builder()
            .id(userPostComment.getId())
            .userPostId(userPost == null ? null : userPost.getId())
            .userId(user == null ? null : user.getId())
            .status(userPostComment.getStatus().getInitial())
            .userName(userPostComment.getUserName())
            .yelloId(userPostComment.getYelloId())
            .title(userPostComment.getTitle())
            .subtitle(userPostComment.getSubtitle())
            .content(userPostComment.getContent())
            .createdAt(String.valueOf(userPostComment.getCreatedAt()))
            .updatedAt(String.valueOf(userPostComment.getUpdatedAt()))
            .build();
    }
}
