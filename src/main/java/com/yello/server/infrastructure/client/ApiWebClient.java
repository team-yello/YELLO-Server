package com.yello.server.infrastructure.client;

import com.yello.server.domain.purchase.dto.apple.AppleOrderResponse;
import com.yello.server.domain.purchase.dto.apple.AppleTransaction;
import org.springframework.http.ResponseEntity;

public interface ApiWebClient {

    ResponseEntity<AppleOrderResponse> appleGetTransaction(
        AppleTransaction appleTransaction);

    ResponseEntity<AppleOrderResponse> getTransactionByWebClient(
        AppleTransaction appleTransaction,
        String appleUrl);
}
