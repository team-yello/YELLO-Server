package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.EventTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTimeJpaRepository extends JpaRepository<EventTime, Long> {

}
