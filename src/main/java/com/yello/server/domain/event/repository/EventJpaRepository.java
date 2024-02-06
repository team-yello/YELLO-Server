package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<Event, Long> {

}
