package com.yello.server.domain.purchase.dto.apple;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;

@Builder
public record AppleVerifyReceipt(
    @JsonAlias("receipt-data")
    String receiptData,
    String password

) {

}
