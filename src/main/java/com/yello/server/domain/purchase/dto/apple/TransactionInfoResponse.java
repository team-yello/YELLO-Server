package com.yello.server.domain.purchase.dto.apple;

import lombok.Builder;

@Builder
public record TransactionInfoResponse(
    String signedTransactionInfo
) {

}
