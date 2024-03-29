package com.yello.server.domain.purchase.service;

import com.yello.server.domain.purchase.dto.apple.AppleNotificationPayloadVO;
import com.yello.server.domain.purchase.dto.apple.ApplePurchaseVO;
import com.yello.server.domain.purchase.dto.apple.TransactionInfoResponse;
import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.entity.PurchaseState;
import com.yello.server.domain.user.entity.User;
import com.yello.server.infrastructure.slack.dto.response.SlackAppleNotificationResponse;
import org.springframework.http.ResponseEntity;

public interface PurchaseManager {

    Purchase createSubscribe(User user, Gateway gateway, String transactionId, String purchaseToken,
        PurchaseState purchaseState, String rawData);

    Purchase createTicket(User user, ProductType productType, Gateway gateway,
        String transactionId, String purchaseToken, PurchaseState purchaseState, String rawData);

    void handleAppleTransactionError(ResponseEntity<TransactionInfoResponse> response,
        String transactionId);
    
    ApplePurchaseVO getPurchaseData(AppleNotificationPayloadVO payloadVO);

    AppleNotificationPayloadVO decodeApplePayload(String signedPayload);

    Purchase decodeAppleNotificationData(String signedTransactionInfo);

    void changeSubscriptionStatus(AppleNotificationPayloadVO payloadVO);

    void refundAppleInApp(AppleNotificationPayloadVO payloadVO);

    SlackAppleNotificationResponse checkPurchaseDataByAppleSignedPayload(String payload);

    void reSubscribeApple(AppleNotificationPayloadVO payloadVO, String notificationType);
    void expiredSubscribe(AppleNotificationPayloadVO payloadVO);
}
