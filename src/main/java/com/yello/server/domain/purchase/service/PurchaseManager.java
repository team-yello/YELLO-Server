package com.yello.server.domain.purchase.service;

import com.yello.server.domain.purchase.dto.apple.AppleNotificationPayloadVO;
import com.yello.server.domain.purchase.dto.apple.TransactionInfoResponse;
import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.user.entity.User;
import org.springframework.http.ResponseEntity;

public interface PurchaseManager {

    Purchase createSubscribe(User user, Gateway gateway, String transactionId);

    Purchase createTicket(User user, ProductType productType, Gateway gateway,
        String transactionId);

    void handleAppleTransactionError(ResponseEntity<TransactionInfoResponse> response,
        String transactionId);

    AppleNotificationPayloadVO decodeApplePayload(String signedPayload);

    String decodeAppleNotificationData(String signedTransactionInfo);

    void changeSubscriptionStatus(AppleNotificationPayloadVO payloadVO);

    void refundAppleInApp(AppleNotificationPayloadVO payloadVO);
}
