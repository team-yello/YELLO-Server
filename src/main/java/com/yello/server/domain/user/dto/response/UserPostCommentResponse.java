package com.yello.server.domain.user.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record UserPostCommentResponse(
    Long pageCount,
    Long totalCount,
    List<UserPostCommentVO> postCommentList
) {

}
