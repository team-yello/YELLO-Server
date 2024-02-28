package com.yello.server.domain.event.dto.request;

import lombok.Builder;

@Builder
public record EventJoinRequest(
    String tag
) {

}
