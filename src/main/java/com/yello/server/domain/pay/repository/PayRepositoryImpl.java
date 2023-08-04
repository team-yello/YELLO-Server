package com.yello.server.domain.pay.repository;

import com.yello.server.domain.pay.entity.Pay;
import com.yello.server.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PayRepositoryImpl implements PayRepository {

    private final PayJpaRepository payJpaRepository;

    @Override
    public Pay save(Pay pay) {
        return payJpaRepository.save(pay);
    }

    @Override
    public List<Pay> findAllByUserAndOptionIndex(User user, Integer optionIndex) {
        return payJpaRepository.findAllByUserAndOptionIndex(user, optionIndex);
    }
}
