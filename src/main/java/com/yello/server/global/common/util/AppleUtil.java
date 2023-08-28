package com.yello.server.global.common.util;

import com.yello.server.domain.purchase.dto.apple.AppleOrderResponse;
import com.yello.server.domain.purchase.dto.apple.AppleTransaction;
import org.springframework.http.ResponseEntity;

public interface AppleUtil {


    ResponseEntity<AppleOrderResponse> appleGetTransaction(
        AppleTransaction appleTransaction);

    ResponseEntity<AppleOrderResponse> getTransactionByWebClient(
        AppleTransaction appleTransaction,
        String appleUrl);

}
