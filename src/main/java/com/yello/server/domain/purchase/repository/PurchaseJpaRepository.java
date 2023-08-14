package com.yello.server.domain.purchase.repository;

import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseJpaRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findAllByUser(User user);

    Optional<Purchase> findTopByUserAndProductTypeOrderByCreatedAtDesc(User user, ProductType productType);
}
