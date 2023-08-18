package com.yello.server.domain.purchase.service;

import com.yello.server.domain.purchase.entity.Gateway;
import com.yello.server.domain.purchase.entity.ProductType;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.user.entity.User;

public interface PurchaseManager {

    Purchase createSubscribe(User user, Gateway gateway, String transactionId);

    Purchase createTicket(User user, ProductType productType, String transactionId);
}
