package com.yello.server.domain.purchase.service;

import static com.yello.server.global.common.ErrorCode.APPLE_TOKEN_SERVER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_SUBSCRIPTIONS_SUBSCRIPTION_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.NOT_FOUND_TRANSACTION_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.domain.purchase.dto.apple.AppleNotificationPayloadVO;
import com.yello.server.domain.purchase.dto.apple.ApplePayloadDataVO;
import com.yello.server.domain.purchase.dto.apple.ApplePurchaseVO;
import com.yello.server.domain.purchase.dto.apple.TransactionInfoResponse;
import com.yello.server.domain.purchase.dto.response.AppleJwsTransactionResponse;
import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.entity.PurchaseState;
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
import com.yello.server.infrastructure.slack.dto.response.SlackAppleNotificationResponse;
import java.util.Map;
import java.util.Optional;
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
    public Purchase createSubscribe(User user, Gateway gateway, String transactionId, String purchaseToken,
        PurchaseState purchaseState, String rawData) {
        user.setSubscribe(Subscribe.ACTIVE);
        Purchase newPurchase =
            Purchase.createPurchase(user, ProductType.YELLO_PLUS, gateway, transactionId, purchaseToken, purchaseState,
                rawData);
        user.addTicketCount(3);
        return purchaseRepository.save(newPurchase);
    }

    @Override
    public Purchase createTicket(User user, ProductType productType, Gateway gateway, String transactionId,
        String purchaseToken, PurchaseState purchaseState, String rawData) {
        Purchase newPurchase =
            Purchase.createPurchase(user, productType, gateway, transactionId, purchaseToken, purchaseState, rawData);
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

        String notificationType = jsonPayload.get("notificationType").toString();
        String subtype =
            (jsonPayload.get("subtype") != null) ? jsonPayload.get("subtype").toString() : null;
        Map<String, Object> data = (Map<String, Object>) jsonPayload.get("data");

        String notificationUUID =
            (jsonPayload.get("notificationUUID") != null) ? jsonPayload.get("notificationUUID")
                .toString() : null;

        ApplePayloadDataVO payloadVO = objectMapper.convertValue(data, ApplePayloadDataVO.class);

        return AppleNotificationPayloadVO.of(notificationType, subtype, payloadVO,
            notificationUUID);
    }

    @Override
    public Purchase decodeAppleNotificationData(String signedTransactionInfo) {

        Map<String, Object> decodeToken = DecodeTokenFactory.decodeToken(signedTransactionInfo);
        String decodeTransactionId = decodeToken.get("transactionId").toString();

        Purchase purchase = purchaseRepository.findByTransactionId(decodeTransactionId)
            .orElseThrow(() -> new PurchaseConflictException(NOT_FOUND_TRANSACTION_EXCEPTION));

        return purchase;
    }

    @Override
    public void changeSubscriptionStatus(AppleNotificationPayloadVO payloadVO) {

        ApplePurchaseVO purchaseData = getPurchaseData(payloadVO);
        Purchase purchase =
            purchaseRepository.findByTransactionId(purchaseData.transactionId())
                .orElseThrow(() -> new PurchaseNotFoundException(NOT_FOUND_TRANSACTION_EXCEPTION));
        User user = purchaseData.purchase().getUser();

        if (payloadVO.subtype().equals(APPLE_SUBTYPE_AUTO_RENEW_DISABLED)
            && !user.getSubscribe().equals(Subscribe.NORMAL)) {
            user.setSubscribe(Subscribe.CANCELED);
            purchase.setPurchaseState(PurchaseState.CANCELED);
        }

        if (payloadVO.subtype().equals(APPLE_SUBTYPE_VOLUNTARY)) {
            user.setSubscribe(Subscribe.NORMAL);
            purchase.setPurchaseState(PurchaseState.PAUSED);
        }

        if(payloadVO.subtype().equals(APPLE_SUBTYPE_AUTO_RENEW_ENABLED)) {
            user.setSubscribe(Subscribe.ACTIVE);
        }
    }

    @Override
    public void refundAppleInApp(AppleNotificationPayloadVO payloadVO) {
        ApplePurchaseVO purchaseData = getPurchaseData(payloadVO);
        Purchase purchase =
            purchaseRepository.findByTransactionId(purchaseData.transactionId())
                .orElseThrow(() -> new PurchaseNotFoundException(NOT_FOUND_TRANSACTION_EXCEPTION));
        User user = purchaseData.purchase().getUser();

        switch (purchaseData.purchase().getProductType()) {
            case YELLO_PLUS -> {
                user.setSubscribe(Subscribe.NORMAL);
                purchase.setPurchaseState(PurchaseState.INACTIVE);
            }
            case ONE_TICKET -> {
                validateTicketCount(REFUND_ONE_TICKET, user);
                purchase.setPurchaseState(PurchaseState.INACTIVE);
            }
            case TWO_TICKET -> {
                validateTicketCount(REFUND_TWO_TICKET, user);
                purchase.setPurchaseState(PurchaseState.INACTIVE);
            }
            case FIVE_TICKET -> {
                validateTicketCount(REFUND_FIVE_TICKET, user);
                purchase.setPurchaseState(PurchaseState.INACTIVE);
            }
        }
    }

    @Override
    public SlackAppleNotificationResponse checkPurchaseDataByAppleSignedPayload(String payload) {
        AppleNotificationPayloadVO payloadVO = decodeApplePayload(payload);
        Purchase purchase = decodeAppleNotificationData(payloadVO.data().signedTransactionInfo());

        return SlackAppleNotificationResponse.of(payloadVO, purchase);
    }

    @Override
    public void reSubscribeApple(AppleNotificationPayloadVO payloadVO, String notificationType) {

        if (notificationType.equals(APPLE_NOTIFICATION_SUBSCRIBED) && !payloadVO.subtype().equals(APPLE_SUBTYPE_RESUBSCRIBE)) {
            return;
        }

        AppleJwsTransactionResponse appleJwtDecode =
            decodeAppleDataPayload(payloadVO.data().signedTransactionInfo());

        Purchase purchase =
            purchaseRepository.findByTransactionId(appleJwtDecode.originalTransactionId())
                .orElseThrow(() -> new PurchaseConflictException(NOT_FOUND_TRANSACTION_EXCEPTION));

        Purchase reSubscribePurchase =
            createSubscribe(purchase.getUser(), Gateway.APPLE, appleJwtDecode.transactionId(), null,
                PurchaseState.ACTIVE, appleJwtDecode.toString());

        purchase.setPurchaseState(PurchaseState.INACTIVE);
        reSubscribePurchase.setPurchaseState(PurchaseState.ACTIVE);

        purchaseRepository.save(reSubscribePurchase);
    }

    public void validateTicketCount(int ticketCount, User user) {
        if (user.getTicketCount() >= ticketCount) {
            user.addTicketCount(-Math.abs(ticketCount));
        }
    }

    @Override
    public ApplePurchaseVO getPurchaseData(AppleNotificationPayloadVO payloadVO) {
        String transactionId =
            decodeAppleNotificationData(
                payloadVO.data().signedTransactionInfo()).getTransactionId();
        Purchase purchase = purchaseRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new PurchaseNotFoundException(NOT_FOUND_TRANSACTION_EXCEPTION));

        return ApplePurchaseVO.of(transactionId, purchase);
    }

    public AppleJwsTransactionResponse decodeAppleDataPayload(String signedTransactionInfo) {

        Map<String, Object> decodeToken = DecodeTokenFactory.decodeToken(signedTransactionInfo);
        String decodeOriginalTransactionId = decodeToken.get("originalTransactionId").toString();
        String decodeTransactionId = decodeToken.get("transactionId").toString();

        return AppleJwsTransactionResponse.of(decodeOriginalTransactionId, decodeTransactionId);
    }
}
