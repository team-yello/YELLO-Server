package com.yello.server.domain.purchase.repository;

import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.user.entity.User;
import java.util.Optional;

public interface PurchaseRepository {

    Optional<Purchase> findTopByUserAndProductTypeOrderByCreatedAtDesc(User use, ProductType productType);
}
