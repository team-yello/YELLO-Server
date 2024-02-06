package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.EventInstanceReward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventInstanceRewardJpaRepository extends JpaRepository<EventInstanceReward, Long> {

}
