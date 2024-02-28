package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<Event, Long> {

    Optional<Event> findTopByTag(EventType type);
}
