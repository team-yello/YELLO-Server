package com.yello.server.domain.pay.sevice;

import com.yello.server.domain.pay.entity.Pay;
import com.yello.server.domain.pay.entity.PayRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.yello.server.global.common.ErrorCode.USERID_NOT_FOUND_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
@Transactional
public class PayServiceImpl implements PayService {

    private final UserRepository userRepository;
    private final PayRepository payRepository;

    @Transactional
    @Override
    public void postPayCount(Long userId, Integer index) {
        User user = findUser(userId);
        System.out.println("121231");
        Optional<Pay> byId = Optional.ofNullable(payRepository.findById(userId).orElseThrow(() -> new UserException(USERID_NOT_FOUND_USER_EXCEPTION)));
        System.out.println("ddddddddddsssssssssssssssaaaaadd");
        Pay payByUserAndIndex = payRepository.findByUserAndIndex(user, index).orElse(payRepository.save(Pay.createPay(index, user)));
        System.out.println("dddddddddddddddd");
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USERID_NOT_FOUND_USER_EXCEPTION));
    }


}
