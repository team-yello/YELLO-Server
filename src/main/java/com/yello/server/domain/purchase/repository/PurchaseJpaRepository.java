package com.yello.server.domain.purchase.repository;

import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.user.entity.User;
import java.time.LocalDateTime;
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
        + "where p.state = 'ACTIVE' "
        + "and p.user = :user "
        + "and p.productType = 'yello_plus' "
        + "order by p.updatedAt DESC")
    Optional<Purchase> findTopByStateAndUser(@Param("user") User user);

    @Query("SELECT count(p) FROM Purchase p WHERE p.gateway = ?1 AND p.productType = ?2 AND ?3 <= p.createdAt AND p.createdAt < ?4")
    Long countByStartAt(Gateway gateway, ProductType productType, LocalDateTime start, LocalDateTime end);

    @Query("SELECT sum(p.price) FROM Purchase p WHERE p.gateway = ?1 AND p.productType = ?2 AND ?3 <= p.createdAt AND p.createdAt < ?4")
    Long countPriceByStartAt(Gateway gateway, ProductType productType, LocalDateTime start, LocalDateTime end);
}
