package com.yello.server.domain.purchase.service;

import com.yello.server.domain.purchase.dto.apple.AppleVerifyReceipt;
import com.yello.server.domain.purchase.dto.apple.AppleVerifyReceiptResponse;
import com.yello.server.domain.purchase.dto.response.UserSubscribeNeededResponse;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
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

    public AppleVerifyReceiptResponse verifyReceipt(Long userId, AppleVerifyReceipt request) {
        AppleVerifyReceiptResponse verifyReceiptResponse = appleUtil.appleVerifyReceipt(request);

        return verifyReceiptResponse;

    }

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
}
