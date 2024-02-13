package com.yello.server.domain.event.repository;

import static com.yello.server.domain.event.entity.QEventReward.eventReward;
import static com.yello.server.domain.event.entity.QEventRewardMapping.eventRewardMapping;
import static com.yello.server.global.common.ErrorCode.EVENT_NOT_FOUND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.EVENT_RANDOM_NOT_FOUND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.NOT_FOUND_EVENT_REWARD_EXCEPTION;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventHistory;
import com.yello.server.domain.event.entity.EventInstance;
import com.yello.server.domain.event.entity.EventInstanceReward;
import com.yello.server.domain.event.entity.EventRandom;
import com.yello.server.domain.event.entity.EventReward;
import com.yello.server.domain.event.entity.EventRewardMapping;
import com.yello.server.domain.event.entity.EventTime;
import com.yello.server.domain.event.entity.EventType;
import com.yello.server.domain.event.exception.EventNotFoundException;
import com.yello.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final EventHistoryJpaRepository eventHistoryJpaRepository;
    private final EventInstanceJpaRepository eventInstanceJpaRepository;
    private final EventInstanceRewardJpaRepository eventInstanceRewardJpaRepository;
    private final EventJpaRepository eventJpaRepository;
    private final EventRandomJpaRepository eventRandomJpaRepository;
    private final EventRewardJpaRepository eventRewardJpaRepository;
    private final EventRewardMappingJpaRepository eventRewardMappingJpaRepository;
    private final EventTimeJpaRepository eventTimeJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public EventHistory save(EventHistory newEventHistory) {
        return eventHistoryJpaRepository.save(newEventHistory);
    }

    @Override
    public EventInstance save(EventInstance newEventInstance) {
        return eventInstanceJpaRepository.save(newEventInstance);
    }

    @Override
    public EventInstanceReward save(EventInstanceReward newEventInstanceReward) {
        return eventInstanceRewardJpaRepository.save(newEventInstanceReward);
    }

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
    public List<EventInstance> findInstanceAllByEventTimeAndUser(EventTime eventTime, User user) {
        return eventInstanceJpaRepository.findAllByEventTimeAndUser(eventTime, user);
    }

    @Override
    public Event getByTag(EventType tag) {
        return eventJpaRepository.findTopByTag(tag)
            .orElseThrow(() -> new EventNotFoundException(EVENT_NOT_FOUND_EXCEPTION));
    }

    @Override
    public EventReward getRewardById(Long eventRewardId) {
        return eventRewardJpaRepository.findById(eventRewardId)
            .orElseThrow(() -> new EventNotFoundException(EVENT_NOT_FOUND_EXCEPTION));
    }

    @Override
    public EventRandom getRandomByRandomTag(String randomTag) {
        return eventRandomJpaRepository.findTopByRandomTag(randomTag)
            .orElseThrow(() -> new EventNotFoundException(EVENT_RANDOM_NOT_FOUND_EXCEPTION));
    }

    @Override
    public Optional<EventHistory> findHistoryByIdempotencyKey(UUID idempotencyKey) {
        return eventHistoryJpaRepository.findTopByIdempotencyKey(idempotencyKey);
    }

    @Override
    public Optional<EventInstance> findInstanceByEventHistory(EventHistory eventHistory) {
        return eventInstanceJpaRepository.findTopByEventHistory(eventHistory);
    }

    @Override
    public EventReward findRewardByTag(String rewardTag) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(eventReward)
            .where(eventReward.tag.eq(rewardTag))
            .fetchOne()).orElseThrow(() -> new EventNotFoundException(NOT_FOUND_EVENT_REWARD_EXCEPTION));
    }

    @Override
    public EventRewardMapping findRewardMappingByEventRewardId(Long eventRewardId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(eventRewardMapping)
            .where(eventRewardMapping.eventReward.id.eq(eventRewardId))
            .fetchFirst()).orElseThrow(() -> new EventNotFoundException(NOT_FOUND_EVENT_REWARD_EXCEPTION));
    }
}
