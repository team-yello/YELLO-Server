package com.yello.server.global.common.dto.response;

import lombok.Builder;

@Builder
public record GoogleTokenIssueResponse(
    String access_token,
    Integer expires_in,
    String scope,
    String token_type
) {

}
