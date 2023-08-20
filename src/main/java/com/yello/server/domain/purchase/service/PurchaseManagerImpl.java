package com.yello.server.domain.purchase.service;

import static com.yello.server.global.common.ErrorCode.APPLE_TOKEN_SERVER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.SUBSCRIBE_ACTIVE_EXCEPTION;

import com.yello.server.domain.purchase.dto.apple.AppleOrderResponse;
import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.exception.AppleTokenServerErrorException;
import com.yello.server.domain.purchase.exception.SubscriptionConflictException;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurchaseManagerImpl implements PurchaseManager {

    private final PurchaseRepository purchaseRepository;

    @Override
    public Purchase createSubscribe(User user, Gateway gateway, String transactionId) {
        user.setSubscribe(Subscribe.ACTIVE);
        Purchase newPurchase =
            Purchase.createPurchase(user, ProductType.YELLO_PLUS, gateway, transactionId);

        return purchaseRepository.save(newPurchase);
    }

    @Override
    public Purchase createTicket(User user, ProductType productType, Gateway gateway, String transactionId) {
        Purchase newPurchase =
            Purchase.createPurchase(user, productType, gateway, transactionId);
        return purchaseRepository.save(newPurchase);
    }

    @Override
    public void handleAppleTransactionError(ResponseEntity<AppleOrderResponse> response,
        String transactionId) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new AppleTokenServerErrorException(APPLE_TOKEN_SERVER_EXCEPTION);
        }
        purchaseRepository.findByTransactionId(transactionId)
            .ifPresent(action -> {
                throw new SubscriptionConflictException(SUBSCRIBE_ACTIVE_EXCEPTION);
            });
    }
}
