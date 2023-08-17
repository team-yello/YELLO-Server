package com.yello.server.global.common.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;

@Builder
@JsonSerialize
public record EmptyObject(

) {

}
