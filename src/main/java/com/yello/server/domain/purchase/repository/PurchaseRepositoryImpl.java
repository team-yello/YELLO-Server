package com.yello.server.domain.purchase.repository;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_USER_SUBSCRIBE_EXCEPTION;

import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.exception.PurchaseNotFoundException;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.ErrorCode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PurchaseRepositoryImpl implements PurchaseRepository {

    public final PurchaseJpaRepository purchaseJpaRepository;

    @Override
    public Purchase save(Purchase purchase) {
        return purchaseJpaRepository.save(purchase);
    }

    @Override
    public Optional<Purchase> findById(Long purchaseId) {
        return purchaseJpaRepository.findById(purchaseId);
    }

    @Override
    public Optional<Purchase> findByTransactionId(String transactionId) {
        return purchaseJpaRepository.findByTransactionId(transactionId);
    }

    @Override
    public Optional<Purchase> findByPurchaseToken(String purchaseToken) {
        return purchaseJpaRepository.findByPurchaseToken(purchaseToken);
    }

    @Override
    public List<Purchase> findAllByUser(User user) {
        return purchaseJpaRepository.findAllByUser(user);
    }

    @Override
    public Optional<Purchase> findTopByUserAndProductTypeOrderByCreatedAtDesc(User user,
        ProductType productType) {
        return purchaseJpaRepository.findTopByUserAndProductTypeOrderByCreatedAtDesc(user,
            productType);
    }

    @Override
    public void delete(Purchase purchase) {
        purchaseJpaRepository.delete(purchase);
    }

    @Override
    public Purchase findEndByStateAndUserId(User user) {
        return purchaseJpaRepository.findTopByStateAndUser(user)
            .orElseThrow(() -> new PurchaseNotFoundException(NOT_FOUND_USER_SUBSCRIBE_EXCEPTION));
    }
}
