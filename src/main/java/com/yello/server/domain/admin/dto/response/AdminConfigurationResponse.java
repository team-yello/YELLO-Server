package com.yello.server.domain.admin.dto.response;

import lombok.Builder;

@Builder
public record AdminConfigurationResponse(
    String tag,
    String value
) {

}
