package com.yello.server.domain.purchase.repository;

import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseJpaRepository extends JpaRepository<Purchase, Long> {

    Optional<Purchase> findByTransactionId(String transactionId);

    Optional<Purchase> findByPurchaseToken(String purchaseToken);

    List<Purchase> findAllByUser(User user);

    Optional<Purchase> findTopByUserAndProductTypeOrderByCreatedAtDesc(User user,
        ProductType productType);

    @Query("select p from Purchase p "
        + "where p.state = 'active' "
        + "and p.user = :user "
        + "and p.productType = 'yello_plus' "
        + "order by p.updatedAt DESC")
    Optional<Purchase> findTopByStateAndUser(@Param("user") User user);
}
