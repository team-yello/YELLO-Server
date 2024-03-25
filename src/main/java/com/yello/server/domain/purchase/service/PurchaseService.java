package com.yello.server.domain.purchase.service;

import static com.yello.server.domain.purchase.entity.ProductType.FIVE_TICKET;
import static com.yello.server.domain.purchase.entity.ProductType.ONE_TICKET;
import static com.yello.server.domain.purchase.entity.ProductType.TWO_TICKET;
import static com.yello.server.domain.purchase.entity.ProductType.YELLO_PLUS;
import static com.yello.server.domain.purchase.entity.ProductType.getProductType;
import static com.yello.server.domain.purchase.entity.ProductType.getTicketAmount;
import static com.yello.server.global.common.ErrorCode.GOOGLE_INAPP_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_NOTIFICATION_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_SUBSCRIPTIONS_FORBIDDEN_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_SUBSCRIPTIONS_SUBSCRIPTION_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_SUBSCRIPTION_DUPLICATED_CANCEL_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_SUBSCRIPTION_TRANSACTION_EXPIRED_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_SUBSCRIPTION_USED_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_TOKEN_FIELD_NOT_FOUND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_TOKEN_FORBIDDEN_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_TOKEN_SERVER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.NOT_FOUND_NOTIFICATION_TYPE_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.NOT_FOUND_TRANSACTION_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.PURCHASE_TOKEN_NOT_FOUND_PURCHASE_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.SUBSCRIBE_ACTIVE_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.APPLE_NOTIFICATION_DID_RENEW;
import static com.yello.server.global.common.util.ConstantUtil.APPLE_NOTIFICATION_EXPIRED;
import static com.yello.server.global.common.util.ConstantUtil.APPLE_NOTIFICATION_REFUND;
import static com.yello.server.global.common.util.ConstantUtil.APPLE_NOTIFICATION_SUBSCRIBED;
import static com.yello.server.global.common.util.ConstantUtil.APPLE_NOTIFICATION_SUBSCRIPTION_STATUS_CHANGE;
import static com.yello.server.global.common.util.ConstantUtil.APPLE_NOTIFICATION_TEST;
import static com.yello.server.global.common.util.ConstantUtil.FIVE_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.ONE_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.TWO_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.YELLO_PLUS_ID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yello.server.domain.purchase.dto.apple.AppleNotificationPayloadVO;
import com.yello.server.domain.purchase.dto.apple.ApplePurchaseVO;
import com.yello.server.domain.purchase.dto.apple.AppleTransaction;
import com.yello.server.domain.purchase.dto.apple.TransactionInfoResponse;
import com.yello.server.domain.purchase.dto.request.AppleInAppRefundRequest;
import com.yello.server.domain.purchase.dto.request.AppleNotificationRequest;
import com.yello.server.domain.purchase.dto.request.GooglePubSubNotificationRequest;
import com.yello.server.domain.purchase.dto.request.GoogleSubscriptionGetRequest;
import com.yello.server.domain.purchase.dto.request.GoogleTicketGetRequest;
import com.yello.server.domain.purchase.dto.request.google.DeveloperNotification;
import com.yello.server.domain.purchase.dto.request.google.OneTimeProductNotificationType;
import com.yello.server.domain.purchase.dto.request.google.SubscriptionNotificationType;
import com.yello.server.domain.purchase.dto.response.GoogleSubscriptionGetResponse;
import com.yello.server.domain.purchase.dto.response.GoogleTicketGetResponse;
import com.yello.server.domain.purchase.dto.response.UserSubscribeNeededResponse;
import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.entity.PurchaseState;
import com.yello.server.domain.purchase.exception.GoogleBadRequestException;
import com.yello.server.domain.purchase.exception.GoogleTokenNotFoundException;
import com.yello.server.domain.purchase.exception.GoogleTokenServerErrorException;
import com.yello.server.domain.purchase.exception.PurchaseConflictException;
import com.yello.server.domain.purchase.exception.PurchaseException;
import com.yello.server.domain.purchase.exception.PurchaseNotFoundException;
import com.yello.server.domain.purchase.exception.SubscriptionConflictException;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.global.common.dto.response.GoogleInAppGetResponse;
import com.yello.server.global.common.dto.response.GoogleTokenIssueResponse;
import com.yello.server.global.common.entity.GoogleToken;
import com.yello.server.global.common.repository.GoogleTokenRepository;
import com.yello.server.global.common.util.ConstantUtil;
import com.yello.server.global.common.util.RestUtil;
import com.yello.server.infrastructure.client.ApiWebClient;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Builder
@Service
@Transactional(readOnly = true)
public class PurchaseService {

    private final ApiWebClient apiWebClient;
    private final GoogleTokenRepository googleTokenRepository;
    private final PurchaseManager purchaseManager;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private String googlePubSubName;

    public PurchaseService(ApiWebClient apiWebClient, GoogleTokenRepository googleTokenRepository,
        PurchaseManager purchaseManager, PurchaseRepository purchaseRepository, UserRepository userRepository,
        @Value("${google.cloud.pub-sub.subscriptions.name}") String googlePubSubName) {
        this.apiWebClient = apiWebClient;
        this.googleTokenRepository = googleTokenRepository;
        this.purchaseManager = purchaseManager;
        this.purchaseRepository = purchaseRepository;
        this.userRepository = userRepository;
        this.googlePubSubName = googlePubSubName;
    }

    public UserSubscribeNeededResponse getUserSubscribe(User user, LocalDateTime time) {
        final Optional<Purchase> mostRecentPurchase =
            purchaseRepository.findTopByUserAndProductTypeOrderByCreatedAtDesc(
                user, YELLO_PLUS);
        final Boolean isSubscribeNeeded = user.getSubscribe() == Subscribe.CANCELED
            && mostRecentPurchase.isPresent()
            && Duration.between(mostRecentPurchase.get().getCreatedAt(), time).getSeconds()
            < 1 * 24 * 60 * 60;

        return UserSubscribeNeededResponse.of(user, isSubscribeNeeded);
    }

    @Transactional
    public void verifyAppleSubscriptionTransaction(Long userId,
        AppleTransaction request) {
        ResponseEntity<TransactionInfoResponse> verifyReceiptResponse =
            apiWebClient.appleGetTransaction(request);
        final User user = userRepository.getById(userId);

        purchaseManager.handleAppleTransactionError(verifyReceiptResponse, request.transactionId());

        if (user.getSubscribe() == Subscribe.ACTIVE) {
            throw new SubscriptionConflictException(SUBSCRIBE_ACTIVE_EXCEPTION);
        }

        if (!request.productId().equals(YELLO_PLUS_ID)) {
            throw new PurchaseException(NOT_FOUND_TRANSACTION_EXCEPTION);
        }

        purchaseManager.createSubscribe(user, Gateway.APPLE, request.transactionId(), null, PurchaseState.ACTIVE,
            verifyReceiptResponse.getBody().toString());

    }

    @Transactional
    public void verifyAppleTicketTransaction(Long userId, AppleTransaction request) {
        final ResponseEntity<TransactionInfoResponse> verifyReceiptResponse =
            apiWebClient.appleGetTransaction(request);

        final User user = userRepository.getById(userId);

        purchaseManager.handleAppleTransactionError(verifyReceiptResponse, request.transactionId());

        switch (request.productId()) {
            case ONE_TICKET_ID:
                purchaseManager.createTicket(user, ONE_TICKET, Gateway.APPLE,
                    request.transactionId(), null, PurchaseState.ACTIVE,
                    verifyReceiptResponse.getBody().toString());
                user.addTicketCount(1);
                break;
            case TWO_TICKET_ID:
                purchaseManager.createTicket(user, TWO_TICKET, Gateway.APPLE,
                    request.transactionId(), null, PurchaseState.ACTIVE,
                    verifyReceiptResponse.getBody().toString());
                user.addTicketCount(2);
                break;
            case FIVE_TICKET_ID:
                purchaseManager.createTicket(user, FIVE_TICKET, Gateway.APPLE,
                    request.transactionId(), null, PurchaseState.ACTIVE,
                    verifyReceiptResponse.getBody().toString());
                user.addTicketCount(5);
                break;
            default:
                throw new PurchaseException(NOT_FOUND_TRANSACTION_EXCEPTION);
        }
    }

    @Transactional
    public GoogleSubscriptionGetResponse verifyGoogleSubscriptionTransaction(Long userId,
        GoogleSubscriptionGetRequest request) throws IOException {
        User user = userRepository.getById(userId);

        // exception
        if (user.getSubscribe() != Subscribe.NORMAL) {
            throw new PurchaseConflictException(GOOGLE_SUBSCRIPTIONS_FORBIDDEN_EXCEPTION);
        }

        purchaseRepository.findByTransactionId(request.orderId().toString())
            .ifPresent(action -> {
                throw new PurchaseConflictException(GOOGLE_SUBSCRIPTION_USED_EXCEPTION);
            });

        final GoogleToken googleToken =
            googleTokenRepository.getById(googleTokenRepository.tokenId);
        if (googleToken.getAccessToken().isEmpty() || googleToken.getRefreshToken().isEmpty()) {
            throw new GoogleTokenNotFoundException(GOOGLE_TOKEN_FIELD_NOT_FOUND_EXCEPTION);
        }

        // logic
        ResponseEntity<String> subscribeResponse = null;
        for (int i = 0; i < 3; i++) {
            subscribeResponse = RestUtil.getGoogleSubscribeCheck(request.purchaseToken(),
                googleToken.getAccessToken());

            if (subscribeResponse.getStatusCode().is2xxSuccessful()) {
                break;
            }

            final String newAccessToken = reissueGoogleAccessToken(googleToken.getRefreshToken());
            googleToken.updateAccessToken(newAccessToken);
        }

        // exception
        if (!subscribeResponse.getStatusCode().is2xxSuccessful()) {
            throw new GoogleTokenServerErrorException(GOOGLE_TOKEN_SERVER_EXCEPTION);
        }

        Gson gson = new Gson();
        JsonObject object = gson.fromJson(subscribeResponse.getBody(), JsonObject.class);
        final String subscriptionState =
            object.get("subscriptionState").toString().replaceAll("\"", "");

        switch (subscriptionState) {
            case ConstantUtil.GOOGLE_PURCHASE_SUBSCRIPTION_EXPIRED -> {
                user.setSubscribe(Subscribe.NORMAL);
                throw new GoogleBadRequestException(
                    GOOGLE_SUBSCRIPTION_TRANSACTION_EXPIRED_EXCEPTION);
            }
            case ConstantUtil.GOOGLE_PURCHASE_SUBSCRIPTION_CANCELED -> {
                if (user.getSubscribe() == Subscribe.CANCELED) {
                    throw new GoogleBadRequestException(
                        GOOGLE_SUBSCRIPTION_DUPLICATED_CANCEL_EXCEPTION);
                } else {
                    // TODO messageQueue 를 이용한 결제 만료일 도달 시, 유저 구독 상태 변경하기
                    user.setSubscribe(Subscribe.CANCELED);
                }
            }
            case ConstantUtil.GOOGLE_PURCHASE_SUBSCRIPTION_ACTIVE -> {
                final Purchase subscribe =
                    purchaseManager.createSubscribe(user, Gateway.GOOGLE, request.orderId(), request.purchaseToken(),
                        PurchaseState.ACTIVE, subscribeResponse.getBody().toString());
                subscribe.setTransactionId(request.orderId());
            }
        }

        return GoogleSubscriptionGetResponse.of(request.productId());
    }

    @Transactional
    public GoogleTicketGetResponse verifyGoogleTicketTransaction(Long userId,
        GoogleTicketGetRequest request)
        throws IOException {
        final User user = userRepository.getById(userId);

        purchaseRepository.findByTransactionId(request.orderId())
            .ifPresent(action -> {
                throw new PurchaseConflictException(GOOGLE_SUBSCRIPTIONS_SUBSCRIPTION_EXCEPTION);
            });

        final GoogleToken googleToken =
            googleTokenRepository.getById(googleTokenRepository.tokenId);
        if (googleToken.getAccessToken().isEmpty() || googleToken.getRefreshToken().isEmpty()) {
            throw new GoogleTokenNotFoundException(GOOGLE_TOKEN_FIELD_NOT_FOUND_EXCEPTION);
        }

        ResponseEntity<GoogleInAppGetResponse> inAppResponse = null;
        for (int i = 0; i < 3; i++) {
            inAppResponse = RestUtil.getGoogleTicketCheck(
                request.productId(), request.purchaseToken(), googleToken.getAccessToken());

            if (inAppResponse.getStatusCode().is2xxSuccessful()) {
                break;
            }

            final String newAccessToken = reissueGoogleAccessToken(googleToken.getRefreshToken());
            googleToken.updateAccessToken(newAccessToken);
        }

        // exception
        if (!inAppResponse.getStatusCode().is2xxSuccessful()) {
            throw new GoogleTokenServerErrorException(GOOGLE_TOKEN_SERVER_EXCEPTION);
        }

        if (inAppResponse.getBody().purchaseState() == 0) {
            purchaseRepository.findByTransactionId(inAppResponse.getBody().orderId())
                .ifPresent(action -> {
                    throw new PurchaseConflictException(
                        GOOGLE_SUBSCRIPTIONS_SUBSCRIPTION_EXCEPTION);
                });

            Purchase ticket =
                purchaseManager.createTicket(user, getProductType(request.productId()),
                    Gateway.GOOGLE, request.orderId(), request.purchaseToken(), PurchaseState.ACTIVE,
                    inAppResponse.getBody().toString());
            user.addTicketCount(getTicketAmount(getProductType(request.productId())) * request.quantity());
            ticket.setTransactionId(inAppResponse.getBody().orderId());
        } else {
            throw new GoogleBadRequestException(GOOGLE_INAPP_BAD_REQUEST_EXCEPTION);
        }

        return GoogleTicketGetResponse.of(request.productId(), user);
    }

    public String reissueGoogleAccessToken(String refreshToken) throws IOException {
        final ResponseEntity<GoogleTokenIssueResponse> reissueResponse =
            RestUtil.postGoogleTokenReissue(
                refreshToken);
        if (!reissueResponse.getStatusCode().is2xxSuccessful()) {
            throw new GoogleTokenNotFoundException(GOOGLE_TOKEN_FORBIDDEN_EXCEPTION);
        }

        return reissueResponse.getBody().access_token();
    }

    @Transactional
    public void refundInAppApple(Long userId, AppleInAppRefundRequest request) {
        final User user = userRepository.getById(userId);

        // 에러 코드 수정
        Purchase purchase = purchaseRepository.findByTransactionId(request.transactionId())
            .orElseThrow(() -> new PurchaseNotFoundException(NOT_FOUND_TRANSACTION_EXCEPTION));

        purchaseRepository.delete(purchase);
        user.setSubscribe(Subscribe.NORMAL);
    }

    @Transactional
    public void appleNotification(AppleNotificationRequest request) {

        AppleNotificationPayloadVO payloadVO =
            purchaseManager.decodeApplePayload(request.signedPayload());

        ApplePurchaseVO purchaseData = purchaseManager.getPurchaseData(payloadVO);
        purchaseRepository.findByTransactionId(purchaseData.transactionId())
            .ifPresent((purchase) -> {
                purchase.setRawData(purchaseData.toString());
            });

        switch (payloadVO.notificationType()) {
            case APPLE_NOTIFICATION_SUBSCRIPTION_STATUS_CHANGE -> {
                purchaseManager.changeSubscriptionStatus(payloadVO);
            }
            case APPLE_NOTIFICATION_EXPIRED -> {
                purchaseManager.expiredSubscribe(payloadVO);
            }
            case APPLE_NOTIFICATION_REFUND -> {
                purchaseManager.refundAppleInApp(payloadVO);
            }
            case APPLE_NOTIFICATION_SUBSCRIBED, APPLE_NOTIFICATION_DID_RENEW -> {
                purchaseManager.reSubscribeApple(payloadVO, payloadVO.notificationType());
            }
            case APPLE_NOTIFICATION_TEST -> {
                return;
            }
            default -> {
                throw new PurchaseNotFoundException(NOT_FOUND_NOTIFICATION_TYPE_EXCEPTION);
            }
        }
    }

    @Transactional
    public Map.Entry<DeveloperNotification, Optional<Purchase>> googleNotification(
        GooglePubSubNotificationRequest request) {
        if (!Objects.equals(request.subscription(), googlePubSubName)) {
            log.info("BAD REQUEST by wrong pub-sub name");
            throw new GoogleBadRequestException(GOOGLE_NOTIFICATION_BAD_REQUEST_EXCEPTION);
        }

        Gson gson = new Gson();

        final String data = new String(
            Base64.getDecoder()
                .decode(request.message().data())
        );

        final DeveloperNotification developerNotification = gson.fromJson(data, DeveloperNotification.class);
        Optional<Purchase> purchase = Optional.empty();

        if (ObjectUtils.isEmpty(developerNotification.getProductType())) {
            log.info("BAD REQUEST by wrong pub-sub name");
            throw new GoogleBadRequestException(GOOGLE_NOTIFICATION_BAD_REQUEST_EXCEPTION);
        }
        log.info(String.format("notification product type is %s", developerNotification.getProductType().toString()));

        switch (developerNotification.getProductType()) {

            case TEST -> {
                log.info("TEST notification is sent by Google Play Console");
            }
            case YELLO_PLUS -> {
                purchase = purchaseRepository.findByPurchaseToken(
                    developerNotification.subscriptionNotification().purchaseToken());

                if (purchase.isEmpty()) {
                    log.info("notification is rejected by INVALID PurchaseToken");
                    throw new PurchaseNotFoundException(PURCHASE_TOKEN_NOT_FOUND_PURCHASE_EXCEPTION);
                }
                purchase.get().setRawData(data);

                SubscriptionNotificationType subscriptionNotificationType = developerNotification
                    .subscriptionNotification()
                    .notificationType();

                log.info(String.format("notification is type of %s", subscriptionNotificationType.toString()));
                switch (subscriptionNotificationType) {
                    case SUBSCRIPTION_RECOVERED, SUBSCRIPTION_RENEWED, SUBSCRIPTION_PURCHASED, SUBSCRIPTION_DEFERRED -> {
                        // ACTIVE
                        purchase.get().setPurchaseState(PurchaseState.ACTIVE);
                        purchase.get().getUser().setSubscribe(Subscribe.ACTIVE);
                    }
                    case SUBSCRIPTION_CANCELED, SUBSCRIPTION_RESTARTED, SUBSCRIPTION_REVOKED -> {
                        // CANCELED
                        purchase.get().setPurchaseState(PurchaseState.CANCELED);
                        purchase.get().getUser().setSubscribe(Subscribe.CANCELED);
                    }
                    case SUBSCRIPTION_ON_HOLD, SUBSCRIPTION_IN_GRACE_PERIOD, SUBSCRIPTION_PAUSED, SUBSCRIPTION_PAUSE_SCHEDULE_CHANGED -> {
                        // PAUSED
                        purchase.get().setPurchaseState(PurchaseState.PAUSED);
                        purchase.get().getUser().setSubscribe(Subscribe.NORMAL);
                    }
                    case SUBSCRIPTION_EXPIRED -> {
                        // INACTIVE
                        purchase.get().setPurchaseState(PurchaseState.INACTIVE);
                        purchase.get().getUser().setSubscribe(Subscribe.NORMAL);
                    }
                    case SUBSCRIPTION_PRICE_CHANGE_CONFIRMED -> {
                        // NOTHING
                    }
                }
            }
            case ONE_TICKET, TWO_TICKET, FIVE_TICKET -> {
                purchase = purchaseRepository.findByPurchaseToken(
                    developerNotification.oneTimeProductNotification().purchaseToken());

                if (purchase.isEmpty()) {
                    log.info("notification is rejected by INVALID PurchaseToken");
                    throw new PurchaseNotFoundException(PURCHASE_TOKEN_NOT_FOUND_PURCHASE_EXCEPTION);
                }
                purchase.get().setRawData(data);

                OneTimeProductNotificationType notificationType = developerNotification
                    .oneTimeProductNotification()
                    .notificationType();

                log.info(String.format("notification is type of %s", notificationType.toString()));
                switch (notificationType) {
                    case ONE_TIME_PRODUCT_PURCHASED -> {
                        purchase.get().setPurchaseState(PurchaseState.ACTIVE);
                    }
                    case ONE_TIME_PRODUCT_CANCELED -> {
                        purchase.get().setPurchaseState(PurchaseState.INACTIVE);
                        purchase.get().getUser()
                            .subTicketCount(getTicketAmount(developerNotification.getProductType()));
                    }
                }
            }
        }
        return Map.entry(developerNotification, purchase);
    }

    public Purchase getByTransactionId(String transactionId) {
        return purchaseRepository.getByTransactionId(transactionId);
    }
}
