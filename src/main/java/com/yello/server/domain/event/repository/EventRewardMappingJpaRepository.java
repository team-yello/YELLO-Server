package com.yello.server.domain.event.repository;

import com.yello.server.domain.event.entity.EventRewardMapping;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventRewardMappingJpaRepository extends JpaRepository<EventRewardMapping, Long> {

    @Query("select e from EventRewardMapping e where e.eventTime.id = ?1")
    List<EventRewardMapping> findAllByEventTimeId(Long eventTimeId);

}
