package com.yello.server.domain.pay.repository;

import com.yello.server.domain.pay.entity.Pay;
import com.yello.server.domain.user.entity.User;
import java.util.List;

public interface PayRepository {

    Pay save(Pay pay);

    List<Pay> findAllByUserAndOptionIndex(User user, Integer optionIndex);
}
