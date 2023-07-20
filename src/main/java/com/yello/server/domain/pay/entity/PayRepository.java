package com.yello.server.domain.pay.entity;

import com.yello.server.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayRepository extends JpaRepository<Pay, Long> {

    List<Pay> findAllByUserAndOptionIndex(User user, Integer optionIndex);
}
