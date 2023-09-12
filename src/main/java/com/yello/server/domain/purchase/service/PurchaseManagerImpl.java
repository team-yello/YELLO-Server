package com.yello.server.domain.purchase.service;

import static com.yello.server.global.common.ErrorCode.APPLE_TOKEN_SERVER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_SUBSCRIPTIONS_SUBSCRIPTION_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.NOT_FOUND_TRANSACTION_EXCEPTION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.domain.purchase.dto.apple.AppleNotificationPayloadVO;
import com.yello.server.domain.purchase.dto.apple.ApplePayloadDataVO;
import com.yello.server.domain.purchase.dto.apple.TransactionInfoResponse;
import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.exception.AppleTokenServerErrorException;
import com.yello.server.domain.purchase.exception.PurchaseConflictException;
import com.yello.server.domain.purchase.exception.PurchaseNotFoundException;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.global.common.factory.DecodeTokenFactory;
import com.yello.server.global.common.factory.TokenFactory;
import com.yello.server.global.common.util.ConstantUtil;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurchaseManagerImpl implements PurchaseManager {

    private final PurchaseRepository purchaseRepository;
    private final TokenFactory tokenFactory;
    private final UserRepository userRepository;

    @Override
    public Purchase createSubscribe(User user, Gateway gateway, String transactionId) {
        user.setSubscribe(Subscribe.ACTIVE);
        Purchase newPurchase =
            Purchase.createPurchase(user, ProductType.YELLO_PLUS, gateway, transactionId);

        return purchaseRepository.save(newPurchase);
    }

    @Override
    public Purchase createTicket(User user, ProductType productType, Gateway gateway,
        String transactionId) {
        Purchase newPurchase =
            Purchase.createPurchase(user, productType, gateway, transactionId);
        return purchaseRepository.save(newPurchase);
    }

    @Override
    public void handleAppleTransactionError(ResponseEntity<TransactionInfoResponse> response,
        String transactionId) {
        tokenFactory.decodeTransactionToken(response.getBody().signedTransactionInfo(),
            transactionId);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new AppleTokenServerErrorException(APPLE_TOKEN_SERVER_EXCEPTION);
        }
        purchaseRepository.findByTransactionId(transactionId)
            .ifPresent(action -> {
                throw new PurchaseConflictException(GOOGLE_SUBSCRIPTIONS_SUBSCRIPTION_EXCEPTION);
            });
    }

    @Override
    public AppleNotificationPayloadVO decodeApplePayload(String signedPayload) {
        
        Map<String, Object> jsonPayload = DecodeTokenFactory.decodeToken(signedPayload);
        ObjectMapper objectMapper = new ObjectMapper();

        System.out.println(jsonPayload + " ??????ddddd");

        String notificationType = jsonPayload.get("notificationType").toString();
        String subType =
            (jsonPayload.get("subType")!=null) ? jsonPayload.get("subType").toString() : null;
        Map<String, Object> data = (Map<String, Object>) jsonPayload.get("data");

        String notificationUUID =
            (jsonPayload.get("notificationUUID")!=null) ? jsonPayload.get("notificationUUID")
                .toString() : null;

        ApplePayloadDataVO payloadVO = objectMapper.convertValue(data, ApplePayloadDataVO.class);

        return AppleNotificationPayloadVO.of(notificationType, subType, payloadVO,
            notificationUUID);
    }

    @Override
    public String decodeAppleNotificationData(String signedTransactionInfo) {

        Map<String, Object> decodeToken = DecodeTokenFactory.decodeToken(signedTransactionInfo);
        String decodeTransactionId = decodeToken.get("transactionId").toString();

        Purchase purchase = purchaseRepository.findByTransactionId(decodeTransactionId)
            .orElseThrow(() -> new PurchaseConflictException(NOT_FOUND_TRANSACTION_EXCEPTION));

        return purchase.getTransactionId();
    }

    @Override
    public void changeSubscriptionStatus(AppleNotificationPayloadVO payloadVO) {

        String transactionId =
            decodeAppleNotificationData(payloadVO.data().signedTransactionInfo());
        Purchase purchase = purchaseRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new PurchaseNotFoundException(NOT_FOUND_TRANSACTION_EXCEPTION));

        User user = purchase.getUser();

        if (payloadVO.subType().equals(ConstantUtil.APPLE_SUBTYPE_AUTO_RENEW_DISABLED)
            && !user.getSubscribe().equals(Subscribe.NORMAL)) {
            user.setSubscribe(Subscribe.NORMAL);
        }
    }


}
