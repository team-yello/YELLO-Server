package com.yello.server.domain.admin.dto.request;

import lombok.Builder;

@Builder
public record AdminLoginRequest(
    String password
) {

}
