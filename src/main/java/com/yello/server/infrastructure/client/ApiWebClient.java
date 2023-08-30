package com.yello.server.infrastructure.client;

import com.yello.server.domain.purchase.dto.apple.AppleTransaction;
import com.yello.server.domain.purchase.dto.apple.TransactionInfoResponse;
import org.springframework.http.ResponseEntity;

public interface ApiWebClient {

    ResponseEntity<TransactionInfoResponse> appleGetTransaction(
        AppleTransaction appleTransaction);

    ResponseEntity<TransactionInfoResponse> getTransactionByWebClient(
        AppleTransaction appleTransaction,
        String appleUrl);
}
