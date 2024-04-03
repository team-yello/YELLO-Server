package com.yello.server.domain.purchase.repository;

import static com.yello.server.global.common.ErrorCode.NOT_EQUAL_TRANSACTION_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.NOT_FOUND_USER_SUBSCRIBE_EXCEPTION;

import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.exception.PurchaseNotFoundException;
import com.yello.server.domain.user.entity.User;
import java.time.LocalDateTime;
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
    public Purchase getByTransactionId(String transactionId) {
        return purchaseJpaRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new PurchaseNotFoundException(NOT_EQUAL_TRANSACTION_EXCEPTION));
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
    public Optional<Purchase> getTopByStateAndUserId(User user) {
        return purchaseJpaRepository.findTopByStateAndUser(user);
    }

    @Override
    public Long countByStartAt(Gateway gateway, ProductType productType, LocalDateTime start, LocalDateTime end) {
        return purchaseJpaRepository.countByStartAt(gateway, productType, start, end);
    }

    @Override
    public Long countPriceByStartAt(Gateway gateway, ProductType productType, LocalDateTime start, LocalDateTime end) {
        return purchaseJpaRepository.countPriceByStartAt(gateway, productType, start, end);
    }
}
