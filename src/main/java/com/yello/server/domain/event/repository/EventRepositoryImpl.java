package com.yello.server.domain.event.repository;

import static com.yello.server.global.common.ErrorCode.EVENT_NOT_FOUND_EXCEPTION;

import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventReward;
import com.yello.server.domain.event.entity.EventRewardMapping;
import com.yello.server.domain.event.entity.EventTime;
import com.yello.server.domain.event.exception.EventNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final EventJpaRepository eventJpaRepository;
    private final EventRewardJpaRepository eventRewardJpaRepository;
    private final EventRewardMappingJpaRepository eventRewardMappingJpaRepository;
    private final EventTimeJpaRepository eventTimeJpaRepository;

    @Override
    public List<Event> findAll() {
        return eventJpaRepository.findAll();
    }

    @Override
    public List<EventTime> findAllByEventId(Long eventId) {
        return eventTimeJpaRepository.findAllByEventId(eventId);
    }

    @Override
    public List<EventRewardMapping> findAllByEventTimeId(Long eventTimeId) {
        return eventRewardMappingJpaRepository.findAllByEventTimeId(eventTimeId);
    }

    @Override
    public List<EventReward> findRewardAll() {
        return eventRewardJpaRepository.findAll();
    }

    @Override
    public EventReward getRewardById(Long eventRewardId) {
        return eventRewardJpaRepository.findById(eventRewardId)
            .orElseThrow(() -> new EventNotFoundException(EVENT_NOT_FOUND_EXCEPTION));
    }
}
