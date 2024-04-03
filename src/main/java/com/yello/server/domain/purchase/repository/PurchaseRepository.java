package com.yello.server.domain.purchase.repository;

import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PurchaseRepository {

    Purchase save(Purchase purchase);

    Optional<Purchase> findById(Long purchaseId);

    Optional<Purchase> findByTransactionId(String transactionId);

    Purchase getByTransactionId(String transactionId);

    Optional<Purchase> findByPurchaseToken(String purchaseToken);

    List<Purchase> findAllByUser(User user);

    Optional<Purchase> findTopByUserAndProductTypeOrderByCreatedAtDesc(User user,
        ProductType productType);

    void delete(Purchase purchase);

    Optional<Purchase> getTopByStateAndUserId(User user);

    Long countByStartAt(Gateway gateway, ProductType productType, LocalDateTime start, LocalDateTime end);

    Long countPriceByStartAt(Gateway gateway, ProductType productType, LocalDateTime start, LocalDateTime end);
}
