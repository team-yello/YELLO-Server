package com.yello.server.domain.admin.dto.response;

import lombok.Builder;

@Builder
public record AdminConfigurationUpdateRequest(
    String tag,
    String value
) {

}
