package com.yello.server.domain.purchase.service;

import com.yello.server.domain.purchase.dto.apple.AppleOrderResponse;
import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.user.entity.User;
import org.springframework.http.ResponseEntity;

public interface PurchaseManager {

    Purchase createSubscribe(User user, Gateway gateway, String transactionId);

    Purchase createTicket(User user, ProductType productType, Gateway gateway, String transactionId);

    void handleAppleTransactionError(ResponseEntity<AppleOrderResponse> response,
        String transactionId);
}
