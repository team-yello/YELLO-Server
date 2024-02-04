package com.yello.server.domain.admin.repository;

import static com.yello.server.domain.event.entity.QEvent.event;
import static com.yello.server.domain.event.entity.QEventTime.eventTime;
import static com.yello.server.global.common.ErrorCode.EVENT_NOT_FOUND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.EVENT_REWARD_NOT_FOUND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.EVENT_TIME_NOT_FOUND_EXCEPTION;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yello.server.domain.admin.exception.UserAdminNotFoundException;
import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventReward;
import com.yello.server.domain.event.entity.EventRewardMapping;
import com.yello.server.domain.event.entity.EventRewardType;
import com.yello.server.domain.event.entity.EventTime;
import com.yello.server.domain.event.repository.EventJpaRepository;
import com.yello.server.domain.event.repository.EventRewardJpaRepository;
import com.yello.server.domain.event.repository.EventRewardMappingJpaRepository;
import com.yello.server.domain.event.repository.EventTimeJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminRepository {

    private final EventJpaRepository eventJpaRepository;
    private final EventRewardJpaRepository eventRewardJpaRepository;
    private final EventRewardMappingJpaRepository eventRewardMappingJpaRepository;
    private final EventTimeJpaRepository eventTimeJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Event save(Event newEvent) {
        return eventJpaRepository.save(newEvent);
    }

    @Override
    public EventTime save(EventTime newEventTime) {
        return eventTimeJpaRepository.save(newEventTime);
    }

    @Override
    public EventRewardMapping save(EventRewardMapping newEventRewardMapping) {
        return eventRewardMappingJpaRepository.save(newEventRewardMapping);
    }

    @Override
    public Event getById(Long eventId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(event)
                .where(event.id.eq(eventId))
                .fetchFirst())
            .orElseThrow(() -> new UserAdminNotFoundException(EVENT_NOT_FOUND_EXCEPTION));
    }

    @Override
    public EventTime getEventTimeById(Long eventTimeId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(eventTime)
                .where(eventTime.id.eq(eventTimeId))
                .fetchFirst())
            .orElseThrow(() -> new UserAdminNotFoundException(EVENT_TIME_NOT_FOUND_EXCEPTION));
    }

    /**
     * findByEnum을 QueryDSL을 이용하여 조회시 오류가 발생하여 JPA로 구현한다.
     */
    @Override
    public EventReward getByTag(EventRewardType tag) {
        return eventRewardJpaRepository.findByTag(tag)
            .orElseThrow(() -> new UserAdminNotFoundException(EVENT_REWARD_NOT_FOUND_EXCEPTION));
    }
}
