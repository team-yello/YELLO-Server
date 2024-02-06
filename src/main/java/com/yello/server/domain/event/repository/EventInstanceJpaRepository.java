package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.EventInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventInstanceJpaRepository extends JpaRepository<EventInstance, Long> {

}
