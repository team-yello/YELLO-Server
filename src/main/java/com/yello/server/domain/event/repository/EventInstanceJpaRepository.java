package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.EventHistory;
import com.yello.server.domain.event.entity.EventInstance;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventInstanceJpaRepository extends JpaRepository<EventInstance, Long> {

    @Query("select e from EventInstance e where e.eventHistory = ?1")
    Optional<EventInstance> findTopByEventHistory(EventHistory eventHistory);
}
