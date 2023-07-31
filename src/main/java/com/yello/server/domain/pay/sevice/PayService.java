package com.yello.server.domain.pay.sevice;

import com.yello.server.domain.pay.entity.Pay;
import com.yello.server.domain.pay.entity.PayRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayService {

    private final UserRepository userRepository;
    private final PayRepository payRepository;

    @Transactional
    public void postPayCount(Long userId, Integer optionIndex) {
        User user = userRepository.findById(userId);
        List<Pay> allByUserAndIndex = payRepository.findAllByUserAndOptionIndex(user, optionIndex);
        if (allByUserAndIndex.isEmpty()) {
            payRepository.save(Pay.createPay(optionIndex, user));
        }
    }
}
