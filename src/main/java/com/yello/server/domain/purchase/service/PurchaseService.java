package com.yello.server.domain.purchase.service;

import static com.yello.server.global.common.ErrorCode.GOOGLE_SUBSCRIPTIONS_FORBIDDEN_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_SUBSCRIPTIONS_SUBSCRIPTION_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_SUBSCRIPTION_DUPLICATED_CANCEL_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_SUBSCRIPTION_USED_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_TOKEN_FIELD_NOT_FOUND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_TOKEN_FORBIDDEN_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_TOKEN_SERVER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.NOT_FOUND_TRANSACTION_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.SUBSCRIBE_ACTIVE_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.FIVE_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.ONE_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.TWO_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.YELLO_PLUS_ID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yello.server.domain.purchase.dto.apple.AppleOrderResponse;
import com.yello.server.domain.purchase.dto.apple.AppleTransaction;
import com.yello.server.domain.purchase.dto.request.GoogleSubscriptionV2GetRequest;
import com.yello.server.domain.purchase.dto.response.GoogleSubscriptionV2GetResponse;
import com.yello.server.domain.purchase.dto.response.UserSubscribeNeededResponse;
import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.exception.GoogleBadRequestException;
import com.yello.server.domain.purchase.exception.GoogleTokenNotFoundException;
import com.yello.server.domain.purchase.exception.GoogleTokenServerErrorException;
import com.yello.server.domain.purchase.exception.PurchaseConflictException;
import com.yello.server.domain.purchase.exception.PurchaseException;
import com.yello.server.domain.purchase.exception.SubscriptionConflictException;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.global.common.dto.response.GoogleTokenIssueResponse;
import com.yello.server.global.common.entity.GoogleToken;
import com.yello.server.global.common.repository.GoogleTokenRepository;
import com.yello.server.global.common.util.AppleUtil;
import com.yello.server.global.common.util.ConstantUtil;
import com.yello.server.global.common.util.RestUtil;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Builder
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseService {

    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;
    private final GoogleTokenRepository googleTokenRepository;
    private final PurchaseManager purchaseManager;
    private final AppleUtil appleUtil;

    public UserSubscribeNeededResponse getUserSubscribe(User user, LocalDateTime time) {
        final Optional<Purchase> mostRecentPurchase =
            purchaseRepository.findTopByUserAndProductTypeOrderByCreatedAtDesc(
                user, ProductType.YELLO_PLUS);
        final Boolean isSubscribeNeeded = user.getSubscribe() == Subscribe.CANCELED
            && mostRecentPurchase.isPresent()
            && Duration.between(mostRecentPurchase.get().getCreatedAt(), time).getSeconds()
            < 1 * 24 * 60 * 60;

        return UserSubscribeNeededResponse.of(user, isSubscribeNeeded);
    }

    @Transactional
    public void verifyAppleSubscriptionTransaction(Long userId,
        AppleTransaction request) {
        final AppleOrderResponse verifyReceiptResponse = appleUtil.appleGetTransaction(request);
        final User user = userRepository.getById(userId);

        if (user.getSubscribe() == Subscribe.ACTIVE) {
            throw new SubscriptionConflictException(SUBSCRIBE_ACTIVE_EXCEPTION);
        }

        if (!request.productId().equals(YELLO_PLUS_ID)) {
            throw new PurchaseException(NOT_FOUND_TRANSACTION_EXCEPTION);
        }

        purchaseManager.createSubscribe(user, Gateway.APPLE);
        user.changeTicketCount(3);
    }

    @Transactional
    public void verifyAppleTicketTransaction(Long userId, AppleTransaction request) {
        final AppleOrderResponse verifyReceiptResponse = appleUtil.appleGetTransaction(request);
        final User user = userRepository.getById(userId);

        // 정상적인 구매일 경우
        switch (request.productId()) {
            case ONE_TICKET_ID:
                purchaseManager.createTicket(user, ProductType.ONE_TICKET);
                user.changeTicketCount(1);
                break;
            case TWO_TICKET_ID:
                purchaseManager.createTicket(user, ProductType.TWO_TICKET);
                user.changeTicketCount(2);
                break;
            case FIVE_TICKET_ID:
                purchaseManager.createTicket(user, ProductType.FIVE_TICKET);
                user.changeTicketCount(5);
                break;
            default:
                throw new PurchaseException(NOT_FOUND_TRANSACTION_EXCEPTION);
        }
    }

    @Transactional
    public GoogleSubscriptionV2GetResponse verifyGoogleSubscriptionTransaction(Long userId,
        GoogleSubscriptionV2GetRequest request) throws IOException {
        User user = userRepository.getById(userId);

        // exception
        if (user.getSubscribe() != Subscribe.NORMAL) {
            throw new PurchaseConflictException(GOOGLE_SUBSCRIPTIONS_FORBIDDEN_EXCEPTION);
        }

        purchaseRepository.findByTransactionId(request.orderId())
            .ifPresent(action -> {
                throw new PurchaseConflictException(GOOGLE_SUBSCRIPTIONS_SUBSCRIPTION_EXCEPTION);
            });

        final GoogleToken googleToken = googleTokenRepository.getById(googleTokenRepository.tokenId);
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
        String subscriptionState = object.get("subscriptionState").toString();

        if (subscriptionState.equals(ConstantUtil.GOOGLE_PURCHASE_SUBSCRIPTION_EXPIRED)) {
            throw new GoogleBadRequestException(GOOGLE_SUBSCRIPTION_USED_EXCEPTION);
        } else if (subscriptionState.equals(ConstantUtil.GOOGLE_PURCHASE_SUBSCRIPTION_CANCELED)) {
            if (user.getSubscribe() == Subscribe.CANCELED) {
                throw new GoogleBadRequestException(GOOGLE_SUBSCRIPTION_DUPLICATED_CANCEL_EXCEPTION);
            }
        } else if (subscriptionState.equals(ConstantUtil.GOOGLE_PURCHASE_SUBSCRIPTION_ACTIVE)) {
            final Purchase subscribe = purchaseManager.createSubscribe(user, Gateway.GOOGLE);
            user.changeTicketCount(3);
            subscribe.setTransactionId(request.orderId());
        }

        return GoogleSubscriptionV2GetResponse.of(request.productId());
    }

    public String reissueGoogleAccessToken(String refreshToken) throws IOException {
        final ResponseEntity<GoogleTokenIssueResponse> reissueResponse = RestUtil.postGoogleTokenReissue(
            refreshToken);
        if (!reissueResponse.getStatusCode().is2xxSuccessful()) {
            throw new GoogleTokenNotFoundException(GOOGLE_TOKEN_FORBIDDEN_EXCEPTION);
        }

        return reissueResponse.getBody().access_token();
    }
}
