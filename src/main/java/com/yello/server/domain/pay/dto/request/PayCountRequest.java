package com.yello.server.domain.pay.dto.request;

import lombok.Builder;

@Builder
public record PayCountRequest(
    Integer index
) {

}
