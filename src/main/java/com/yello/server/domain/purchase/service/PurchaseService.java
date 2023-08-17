package com.yello.server.domain.purchase.service;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_TRANSACTION_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.SUBSCRIBE_ACTIVE_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.FIVE_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.ONE_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.TWO_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.YELLO_PLUS_ID;

import com.yello.server.domain.purchase.dto.apple.AppleOrderResponse;
import com.yello.server.domain.purchase.dto.apple.AppleTransaction;
import com.yello.server.domain.purchase.dto.response.UserSubscribeNeededResponse;
import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.exception.PurchaseException;
import com.yello.server.domain.purchase.exception.SubscriptionConflictException;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.global.common.util.AppleUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Builder
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseService {

    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;
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
            System.out.println("sfsdfjsdfsdsss" + request.productId());
            throw new PurchaseException(NOT_FOUND_TRANSACTION_EXCEPTION);
        }

        createSubscribe(user);
        user.ticketPlus(3);
    }

    @Transactional
    public void verifyAppleTicketTransaction(Long userId, AppleTransaction request) {
        final AppleOrderResponse verifyReceiptResponse = appleUtil.appleGetTransaction(request);
        final User user = userRepository.getById(userId);

        // 정상적인 구매일 경우
        switch (request.productId()) {
            case ONE_TICKET_ID:
                createTicket(user, ProductType.ONE_TICKET);
                user.ticketPlus(1);
                break;
            case TWO_TICKET_ID:
                createTicket(user, ProductType.TWO_TICKET);
                user.ticketPlus(2);
                break;
            case FIVE_TICKET_ID:
                createTicket(user, ProductType.FIVE_TICKET);
                user.ticketPlus(5);
                break;
            default:
                throw new PurchaseException(NOT_FOUND_TRANSACTION_EXCEPTION);

        }
    }

    @Transactional
    public void createSubscribe(User user) {

        user.setSubscribe();
        Purchase newPurchase = Purchase.createPurchase(user, ProductType.YELLO_PLUS, Gateway.APPLE);

        purchaseRepository.save(newPurchase);
    }

    @Transactional
    public void createTicket(User user, ProductType productType) {

        Purchase newPurchase = Purchase.createPurchase(user, productType, Gateway.APPLE);
        purchaseRepository.save(newPurchase);
    }
}
