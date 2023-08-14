package com.yello.server.domain.user.dto.request;

import lombok.Builder;

@Builder
public record UserDeviceTokenRequest(
    String deviceToken
) {

}
