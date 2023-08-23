package com.yello.server.domain.purchase;

import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.user.entity.User;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FakePurchaseRepository implements PurchaseRepository {

    private final List<Purchase> data = new ArrayList<>();
    private Long id = 0L;


    @Override
    public Purchase save(Purchase purchase) {
        if (purchase.getId()!=null && purchase.getId() > id) {
            id = purchase.getId();
        }

        final Purchase newPurchase = Purchase.builder()
            .id(purchase.getId()==null ? ++id : purchase.getId())
            .price(purchase.getPrice())
            .user(purchase.getUser())
            .gateway(purchase.getGateway())
            .productType(purchase.getProductType())
            .createdAt(purchase.getCreatedAt())
            .updatedAt(purchase.getUpdatedAt())
            .build();

        data.add(newPurchase);
        return newPurchase;
    }

    @Override
    public Optional<Purchase> findById(Long purchaseId) {
        return data.stream()
            .filter(purchase -> Objects.equals(purchase.getId(), purchaseId))
            .findFirst();
    }

    @Override
    public List<Purchase> findAllByUser(User user) {
        return data.stream()
            .filter(purchase -> purchase.getUser().equals(user))
            .toList();
    }

    @Override
    public Optional<Purchase> findTopByUserAndProductTypeOrderByCreatedAtDesc(User user,
        ProductType productType) {
        return data.stream()
            .filter(purchase -> {
                    return purchase.getUser().getId().equals(user.getId())
                        && purchase.getProductType().equals(productType);
                }
            )
            .sorted(Comparator.comparing(Purchase::getCreatedAt).reversed())
            .findFirst();
    }

    @Override
    public Optional<Purchase> findByTransactionId(String transactionId) {
        return data.stream()
            .filter(purchase -> purchase.getTransactionId().equals(transactionId))
            .findFirst();
    }

    @Override
    public void delete(Purchase purchase) {
        data.remove(purchase);
    }
}
