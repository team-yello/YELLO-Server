package com.yello.server.domain.event.service;

import static com.yello.server.global.common.ErrorCode.DUPLICATE_ADMOB_REWARD_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.EVENT_COUNT_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.EVENT_DATE_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.EVENT_TIME_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.IDEMPOTENCY_KEY_CONFLICT_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.IDEMPOTENCY_KEY_NOT_FOUND_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.GlobalZoneId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.crypto.tink.apps.rewardedads.RewardedAdsVerifier;
import com.yello.server.domain.event.dto.request.AdmobRewardRequest;
import com.yello.server.domain.event.dto.request.AdmobSsvRequest;
import com.yello.server.domain.event.dto.request.EventJoinRequest;
import com.yello.server.domain.event.dto.response.EventResponse;
import com.yello.server.domain.event.dto.response.EventRewardResponse;
import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventHistory;
import com.yello.server.domain.event.entity.EventInstance;
import com.yello.server.domain.event.entity.EventInstanceReward;
import com.yello.server.domain.event.entity.EventRandom;
import com.yello.server.domain.event.entity.EventReward;
import com.yello.server.domain.event.entity.EventRewardMapping;
import com.yello.server.domain.event.entity.EventTime;
import com.yello.server.domain.event.entity.EventType;
import com.yello.server.domain.event.entity.RandomType;
import com.yello.server.domain.event.entity.RewardType;
import com.yello.server.domain.event.exception.EventBadRequestException;
import com.yello.server.domain.event.exception.EventForbiddenException;
import com.yello.server.domain.event.exception.EventNotFoundException;
import com.yello.server.domain.event.repository.EventRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.global.common.factory.UuidFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public List<EventResponse> getEvents(Long userId) throws JsonProcessingException {
        // exception
        final User user = userRepository.getById(userId);

        // logic
        ZonedDateTime now = ZonedDateTime.now(GlobalZoneId);
        OffsetTime nowTime = now.toOffsetDateTime().toOffsetTime();
        List<EventResponse> result = new ArrayList<>();

        // 현재 날짜에 유효한 이벤트.
        final List<Event> eventList = eventRepository.findAll().stream()
            .filter(event -> now.isAfter(event.getStartDate()) && now.isBefore(event.getEndDate()))
            .toList();

        for (Event event : eventList) {
            // 현재 시각에 유효한 이벤트 시간대
            final List<EventTime> eventTimeList =
                eventRepository.findAllByEventId(event.getId()).stream()
                    .filter(
                        eventTime -> nowTime.isAfter(eventTime.getStartTime()) && nowTime.isBefore(
                            eventTime.getEndTime())
                    )
                    .toList();

            List<EventInstance> eventInstanceList = new ArrayList<>();
            if (!eventTimeList.isEmpty()) {
                final EventTime eventTime = eventTimeList.get(0);

                // 현재 시각이 이벤트 시간에 유효하고, 남은 보상 카운트가 0인 이력
                eventInstanceList.addAll(
                    eventRepository.findInstanceAllByEventTimeAndUser(
                            eventTime, user)
                        .stream()
                        .filter(eventInstance ->
                            eventInstance.getInstanceDate().isAfter(event.getStartDate())
                                && eventInstance.getInstanceDate().isBefore(event.getEndDate())
                                && nowTime.isAfter(eventInstance.getEventTime().getStartTime())
                                && nowTime.isBefore(eventInstance.getEventTime().getEndTime())
                                && eventInstance.getRemainEventCount()==0
                        )
                        .toList()
                );
            }

            // 해당 변수로 클라가 이벤트를 띄울지 판단하게 됨.
            // 이벤트 시간대가 있고, 보상 카운트가 0인 참여이력의 숫자가 RewardCount 보다 적을 때
            // k번 이벤트 참여는 보상 카운트로 구현한다.
            boolean isEventAvailable =
                !eventTimeList.isEmpty() && (eventTimeList.get(0).getRewardCount()
                    > eventInstanceList.size());

            final EventTime eventTime = isEventAvailable ? eventTimeList.get(0) : null;
            final List<EventRewardMapping> eventRewardMappingList =
                isEventAvailable ? eventRepository.findAllByEventTimeId(eventTime.getId()) : null;

            result.add(EventResponse.of(event, eventTime, eventRewardMappingList));
        }

        return result;
    }

    @Transactional
    public void joinEvent(Long userId, UUID uuidIdempotencyKey, EventJoinRequest request) {
        // exception
        final User user = userRepository.getById(userId);
        final Optional<EventHistory> eventHistory =
            eventRepository.findHistoryByIdempotencyKey(uuidIdempotencyKey);
        if (eventHistory.isPresent()) {
            throw new EventBadRequestException(IDEMPOTENCY_KEY_CONFLICT_EXCEPTION);
        }

        // logic
        ZonedDateTime now = ZonedDateTime.now(GlobalZoneId);
        OffsetTime nowTime = now.toOffsetDateTime().toOffsetTime();
        final Event event = eventRepository.getByTag(EventType.fromCode(request.tag()));

        // 이벤트 날짜에 유효해야함.
        if (!(now.isAfter(event.getStartDate()) && now.isBefore(event.getEndDate()))) {
            throw new EventBadRequestException(EVENT_DATE_BAD_REQUEST_EXCEPTION);
        }

        // 이벤트 시간대에 유효해야함.
        final List<EventTime> eventTimeList =
            eventRepository.findAllByEventId(event.getId()).stream()
                .filter(eventTime -> nowTime.isAfter(eventTime.getStartTime()) && nowTime.isBefore(
                    eventTime.getEndTime()))
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
            .eventTime(eventTime)
            .instanceDate(now)
            .remainEventCount(1L)
            .build());
    }

    @Transactional
    public EventRewardResponse rewardEvent(Long userId, UUID uuidIdempotencyKey)
        throws JsonProcessingException {
        // exception
        final User user = userRepository.getById(userId);
        final Optional<EventHistory> eventHistory =
            eventRepository.findHistoryByIdempotencyKey(uuidIdempotencyKey);
        if (eventHistory.isEmpty()) {
            throw new EventNotFoundException(IDEMPOTENCY_KEY_NOT_FOUND_EXCEPTION);
        }
        // 멱등키에 해당하는 하나의 EventHistory는 반드시 하나의 EventInstance만 가진다.
        final Optional<EventInstance> eventInstance =
            eventRepository.findInstanceByEventHistory(eventHistory.get());
        ZonedDateTime now = ZonedDateTime.now(GlobalZoneId);
        OffsetTime nowTime = now.toOffsetDateTime().toOffsetTime();

        // 이벤트 참여 요청은 1일 동안만 유효하다.
        final Duration duration = Duration.between(now, eventInstance.get().getInstanceDate());
        if (duration.compareTo(Duration.ofDays(1L)) >= 1) {
            throw new EventBadRequestException(EVENT_DATE_BAD_REQUEST_EXCEPTION);
        }

        // 이벤트 시각이 유효해야한다.
        if (!(nowTime.isAfter(eventInstance.get().getEventTime().getStartTime())
            && nowTime.isBefore(eventInstance.get().getEventTime().getEndTime()))) {
            throw new EventBadRequestException(EVENT_TIME_BAD_REQUEST_EXCEPTION);
        }

        if (eventInstance.get().getRemainEventCount() <= 0L) {
            throw new EventBadRequestException(EVENT_COUNT_BAD_REQUEST_EXCEPTION);
        }

        final List<EventRewardMapping> eventRewardMappingList =
            eventRepository.findAllByEventTimeId(
                eventInstance.get().getEventTime().getId());

        // logic
        // EventRewardProbability 필드에 따라 보상을 선택한다.
        final EventRewardMapping randomRewardMapping = selectRandomly(eventRewardMappingList);
        final long randomValue = selectRandomValue(randomRewardMapping);

        final EventInstanceReward eventInstanceReward =
            eventRepository.save(EventInstanceReward.builder()
                .eventInstance(eventInstance.get())
                .rewardTag(randomRewardMapping.getEventReward().getTag())
                .rewardValue(randomValue)
                .rewardTitle(String.format(randomRewardMapping.getEventReward().getRewardTitle(),
                    randomValue))
                .rewardImage(randomRewardMapping.getEventReward().getRewardImage())
                .build());

        /**
         * TODO 그냥 문자열로 저장하고 있는데, 어떻게 해야 enum의 문제를 극복하면서 switch와 함꼐 사용할 수 있을지 고민해야함.
         */
        if (randomRewardMapping.getEventReward().getTag().equals("TICKET")) {
            user.addTicketCount((int) randomValue);
        } else if (randomRewardMapping.getEventReward().getTag().equals("POINT")) {
            user.addPointBySubscribe((int) randomValue);
        }
        eventInstance.get().subRemainEventCount(1L);
        return EventRewardResponse.of(eventInstanceReward);
    }

    @SneakyThrows
    @Transactional
    public void verifyAdmobReward(URI uri, HttpServletRequest request) {
        // 1. admob 검증하기
        RewardedAdsVerifier verifier = new RewardedAdsVerifier.Builder()
            .fetchVerifyingPublicKeysWith(
                RewardedAdsVerifier.KEYS_DOWNLOADER_INSTANCE_PROD)
            .build();
        verifier.verify(uri.toString());

        // 2. google이 넘겨준 query 정보 가져오기
        AdmobSsvRequest admobRequest = AdmobSsvRequest.of(request.getParameterMap());

        // 3. 보상정보 멱등키랑 저장하기
        UUID uuidIdempotencyKey = UuidFactory.checkUuid(admobRequest.customData());

        final Optional<EventHistory> eventHistory =
            eventRepository.findHistoryByIdempotencyKey(uuidIdempotencyKey);
        if (eventHistory.isPresent()) {
            throw new EventBadRequestException(IDEMPOTENCY_KEY_CONFLICT_EXCEPTION);
        }

        eventRepository.save(EventHistory.of(null, uuidIdempotencyKey));
    }

    @Transactional
    public EventRewardResponse rewardAdmob(Long userId, AdmobRewardRequest request) {
        UUID uuid = UuidFactory.checkUuid(request.uuid());
        final User user = userRepository.getById(userId);

        // 멱등키를 통해 해당 history 찾기
        final Optional<EventHistory> eventHistory =
            eventRepository.findHistoryByIdempotencyKey(uuid);
        if (eventHistory.isEmpty()) {
            throw new EventBadRequestException(IDEMPOTENCY_KEY_NOT_FOUND_EXCEPTION);
        }

        // history 있으면 userId로 세팅
        if (eventHistory.get().getUser()!=null) {
            throw new EventForbiddenException(DUPLICATE_ADMOB_REWARD_EXCEPTION);
        }
        eventHistory.get().update(user);

        // event_random tag 확인 후 reward
        EventReward eventReward = null;
        switch (RandomType.fromCode(request.randomType())) {
            case FIXED, ADMOB_RANDOM -> {
                eventReward = handleRewardByType(request, user);
            }
        }
        EventRewardMapping rewardMapping =
            eventRepository.findRewardMappingByEventRewardId(eventReward.getId());

        // event_instance에 해당 데이터 저장
        EventInstance eventInstance = eventRepository.save(
            EventInstance.of(rewardMapping.getEventTime(), eventHistory.get()));
        EventInstanceReward rewardInstance =
            eventRepository.save(EventInstanceReward.of(eventInstance, eventReward));

        return EventRewardResponse.of(rewardInstance);
    }

    private EventReward handleRewardByType(AdmobRewardRequest request, User user) {
        switch (RewardType.fromCode(request.rewardType())) {
            case ADMOB_POINT -> {
                EventReward rewardByTag = eventRepository.findRewardByTag(request.rewardType());
                user.addPoint(Math.toIntExact((rewardByTag.getMinRewardValue())));
                return rewardByTag;
            }
            case ADMOB_MULTIPLE_POINT -> {
                EventReward rewardByTag = eventRepository.findRewardByTag(request.rewardType());
                user.addPoint(request.rewardNumber());
                rewardByTag.updateMinRewardValue(Long.valueOf(request.rewardNumber() * 2));
                return rewardByTag;
            }
        }
        return null;
    }

    private @NotNull EventRewardMapping selectRandomly(
        @NotEmpty List<EventRewardMapping> eventRewardMappingList) {
        // 전체 확률 합계를 계산합니다.
        int totalProbability = eventRewardMappingList.stream()
            .mapToInt(EventRewardMapping::getEventRewardProbability)
            .sum();

        // 0에서 totalProbability 사이의 무작위 정수를 생성합니다.
        int randomValue = new Random().nextInt(totalProbability);

        // 누적 확률을 계산하며 무작위 값이 어느 구간에 속하는지 찾습니다.
        int cumulativeProbability = 0;
        for (EventRewardMapping eventRewardMapping : eventRewardMappingList) {
            cumulativeProbability += eventRewardMapping.getEventRewardProbability();
            if (randomValue < cumulativeProbability) {
                return eventRewardMapping;
            }
        }

        return eventRewardMappingList.get(0);
    }

    private long selectRandomValue(@NotNull EventRewardMapping eventRewardMapping)
        throws JsonProcessingException {
        EventReward eventReward = eventRewardMapping.getEventReward();
        EventRandom eventRandom = eventRewardMapping.getEventRandom();

        List<ProbabilityPoint> probabilityPoints =
            objectMapper.readValue(eventRandom.getProbabilityPointList(),
                new TypeReference<List<ProbabilityPoint>>() {
                }
            );

        return calculateRewardValue(probabilityPoints, eventReward.getMinRewardValue(),
            eventReward.getMaxRewardValue());
    }

    private long calculateRewardValue(List<ProbabilityPoint> points, long minRewardValue,
        long maxRewardValue) {
        // 무작위 x 값을 생성합니다.
        double x = new Random().nextDouble();

        // x 값이 속하는 구간을 찾습니다.
        for (int i = 0; i < points.size() - 1; i++) {
            if (x >= points.get(i).getX() && x < points.get(i + 1).getX()) {
                // x 값이 속하는 구간을 찾았으므로 해당 구간에서 무작위 보상값을 생성합니다.
                return randomRewardValue(points.get(i).getY(), points.get(i + 1).getY(),
                    minRewardValue,
                    maxRewardValue);
            }
        }

        // x 값이 마지막 구간에 속하는 경우
        if (x >= points.get(points.size() - 1).getX()) {
            return randomRewardValue(points.get(points.size() - 1).getY(), 1.0, minRewardValue,
                maxRewardValue);
        }

        // 이 부분은 도달할 수 없지만 혹시 모를 오류를 대비해 예외를 던집니다.
        throw new IllegalArgumentException("Could not find a matching range for x: " + x);
    }

    private long randomRewardValue(double y1, double y2, long minRewardValue, long maxRewardValue) {
        // y1과 y2 사이의 무작위 y 값을 생성합니다.
        double y = y1 + new Random().nextDouble() * (y2 - y1);

        // 보상값을 계산합니다.
        return (long) ((maxRewardValue - minRewardValue) * y + minRewardValue);
    }

    @Getter
    private static class ProbabilityPoint {

        private double x;
        private double y;
    }
}
