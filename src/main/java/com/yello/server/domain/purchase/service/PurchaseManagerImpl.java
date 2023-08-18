package com.yello.server.domain.purchase.service;

import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurchaseManagerImpl implements PurchaseManager {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;

    public Purchase createSubscribe(User user, Gateway gateway) {

        user.setSubscribe();
        Purchase newPurchase = Purchase.createPurchase(user, ProductType.YELLO_PLUS, gateway);

        return purchaseRepository.save(newPurchase);
    }

    public Purchase createTicket(User user, ProductType productType) {

        Purchase newPurchase = Purchase.createPurchase(user, productType, Gateway.APPLE);
        return purchaseRepository.save(newPurchase);
    }
}
