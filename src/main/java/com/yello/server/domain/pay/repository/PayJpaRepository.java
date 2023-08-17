package com.yello.server.domain.pay.repository;

import com.yello.server.domain.pay.entity.Pay;
import com.yello.server.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayJpaRepository extends JpaRepository<Pay, Long> {

    List<Pay> findAllByUserAndOptionIndex(User user, Integer optionIndex);
}
