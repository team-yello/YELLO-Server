package com.yello.server.domain.event.service;

import static com.yello.server.global.common.ErrorCode.EVENT_DATE_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.EVENT_TIME_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.IDEMPOTENCY_KEY_CONFLICT_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.GlobalZoneId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yello.server.domain.event.dto.request.EventJoinRequest;
import com.yello.server.domain.event.dto.response.EventResponse;
import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventHistory;
import com.yello.server.domain.event.entity.EventInstance;
import com.yello.server.domain.event.entity.EventRewardMapping;
import com.yello.server.domain.event.entity.EventTime;
import com.yello.server.domain.event.entity.EventType;
import com.yello.server.domain.event.exception.EventBadRequestException;
import com.yello.server.domain.event.repository.EventRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public List<EventResponse> getEvents(Long userId) throws JsonProcessingException {
        // exception
        final User user = userRepository.getById(userId);

        // logic
        ZonedDateTime now = ZonedDateTime.now(GlobalZoneId);
        OffsetTime nowTime = now.toOffsetDateTime().toOffsetTime();
        List<EventResponse> result = new ArrayList<>();

        final List<Event> eventList = eventRepository.findAll().stream()
            .filter(event -> now.isAfter(event.getStartDate()) && now.isBefore(event.getEndDate()))
            .toList();

        for (Event event : eventList) {
            final List<EventTime> eventTimeList = eventRepository.findAllByEventId(event.getId()).stream()
                .filter(
                    eventTime -> nowTime.isAfter(eventTime.getStartTime()) && nowTime.isBefore(eventTime.getEndTime())
                )
                .toList();

            final EventTime eventTime = eventTimeList.isEmpty() ? null : eventTimeList.get(0);
            final List<EventRewardMapping> eventRewardMappingList =
                eventTimeList.isEmpty() ? null : eventRepository.findAllByEventTimeId(eventTime.getId());

            result.add(EventResponse.of(event, eventTime, eventRewardMappingList));
        }

        return result;
    }

    @Transactional
    public void joinEvent(Long userId, UUID uuidIdempotencyKey, EventJoinRequest request) {
        // exception
        final User user = userRepository.getById(userId);
        final Optional<EventHistory> eventHistory = eventRepository.findByIdempotencyKey(uuidIdempotencyKey);
        if (eventHistory.isPresent()) {
            throw new EventBadRequestException(IDEMPOTENCY_KEY_CONFLICT_EXCEPTION);
        }

        // logic
        ZonedDateTime now = ZonedDateTime.now(GlobalZoneId);
        OffsetTime nowTime = now.toOffsetDateTime().toOffsetTime();
        final Event event = eventRepository.getByTag(EventType.fromCode(request.tag()));

        if (!(now.isAfter(event.getStartDate()) && now.isBefore(event.getEndDate()))) {
            throw new EventBadRequestException(EVENT_DATE_BAD_REQUEST_EXCEPTION);
        }

        final List<EventTime> eventTimeList = eventRepository.findAllByEventId(event.getId()).stream()
            .filter(eventTime -> nowTime.isAfter(eventTime.getStartTime()) && nowTime.isBefore(eventTime.getEndTime()))
            .toList();
        if (eventTimeList.isEmpty()) {
            throw new EventBadRequestException(EVENT_TIME_BAD_REQUEST_EXCEPTION);
        }
        EventTime eventTime = eventTimeList.get(0);

        final EventHistory newEventHistory = eventRepository.save(EventHistory.builder()
            .idempotencyKey(uuidIdempotencyKey)
            .user(user)
            .build());

        eventRepository.save(EventInstance.builder()
            .eventHistory(newEventHistory)
            .event(event)
            .instanceDate(now)
            .startTime(eventTime.getStartTime())
            .endTime(eventTime.getEndTime())
            .remainEventCount(eventTime.getRewardCount())
            .build());
    }
}
