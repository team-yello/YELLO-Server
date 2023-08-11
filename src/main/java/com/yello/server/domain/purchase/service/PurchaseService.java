package com.yello.server.domain.purchase.service;

import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseService {

    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;

}
