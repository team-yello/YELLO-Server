package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.EventTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventTimeJpaRepository extends JpaRepository<EventTime, Long> {

    @Query("select e from EventTime e where e.event.id = ?1")
    List<EventTime> findAllByEventId(Long eventId);

}
