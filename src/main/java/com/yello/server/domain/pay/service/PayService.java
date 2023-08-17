package com.yello.server.domain.pay.service;

import com.yello.server.domain.pay.entity.Pay;
import com.yello.server.domain.pay.repository.PayRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayService {

    private final UserRepository userRepository;
    private final PayRepository payRepository;

    @Transactional
    public void postPayCount(Long userId, Integer optionIndex) {
        final User user = userRepository.getById(userId);
        final List<Pay> allByUserAndIndex = payRepository.findAllByUserAndOptionIndex(user,
            optionIndex);
        if (allByUserAndIndex.isEmpty()) {
            payRepository.save(Pay.createPay(optionIndex, user));
        }
    }
}
