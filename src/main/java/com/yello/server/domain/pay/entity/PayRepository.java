package com.yello.server.domain.pay.entity;

import com.yello.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayRepository extends JpaRepository<Pay, Long> {

    Optional<Pay> findByUserAndIndex(User user, Integer index);
}
