package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.EventRewardMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRewardMappingJpaRepository extends JpaRepository<EventRewardMapping, Long> {

}
