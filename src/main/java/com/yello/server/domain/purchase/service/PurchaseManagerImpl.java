package com.yello.server.domain.purchase.service;

import static com.yello.server.global.common.ErrorCode.APPLE_TOKEN_SERVER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GOOGLE_SUBSCRIPTIONS_SUBSCRIPTION_EXCEPTION;

import com.yello.server.domain.purchase.dto.apple.TransactionInfoResponse;
import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.exception.AppleTokenServerErrorException;
import com.yello.server.domain.purchase.exception.PurchaseConflictException;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.factory.TokenFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurchaseManagerImpl implements PurchaseManager {

    private final PurchaseRepository purchaseRepository;
    private final TokenFactory tokenFactory;

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


}
