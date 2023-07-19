package com.yello.server.domain.pay.entity;

import com.yello.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayRepository extends JpaRepository<Pay, Long> {

    List<Pay> findAllByUserAndOptionIndex(User user, Integer optionIndex);
}
