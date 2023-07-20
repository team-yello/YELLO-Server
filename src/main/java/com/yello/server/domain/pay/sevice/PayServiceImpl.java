package com.yello.server.domain.pay.sevice;

import static com.yello.server.global.common.ErrorCode.USERID_NOT_FOUND_USER_EXCEPTION;

import com.yello.server.domain.pay.entity.Pay;
import com.yello.server.domain.pay.entity.PayRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayServiceImpl implements PayService {

    private final UserRepository userRepository;
    private final PayRepository payRepository;
 
    @Transactional
    @Override
    public void postPayCount(Long userId, Integer optionIndex) {
        User user = findUser(userId);
        List<Pay> allByUserAndIndex = payRepository.findAllByUserAndOptionIndex(user, optionIndex);
        if (allByUserAndIndex.isEmpty()) {
            payRepository.save(Pay.createPay(optionIndex, user));
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserException(USERID_NOT_FOUND_USER_EXCEPTION));
    }
}
