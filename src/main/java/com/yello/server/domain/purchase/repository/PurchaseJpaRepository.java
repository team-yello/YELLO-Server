package com.yello.server.domain.purchase.repository;

import com.yello.server.domain.purchase.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseJpaRepository extends JpaRepository<Purchase, Long> {

}
