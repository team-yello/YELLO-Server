package com.yello.server.domain.purchase.repository;

import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;

public interface PurchaseRepository {

    Purchase save(Purchase purchase);

    Optional<Purchase> findById(Long purchaseId);

    Optional<Purchase> findByTransactionId(String transactionId);

    List<Purchase> findAllByUser(User user);

    Optional<Purchase> findTopByUserAndProductTypeOrderByCreatedAtDesc(User user,
        ProductType productType);

    void delete(Purchase purchase);
}
