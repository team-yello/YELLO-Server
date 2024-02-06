package com.yello.server.domain.event.medium;

import static com.yello.server.domain.event.entity.EventRewardRandomType.FIXED;
import static com.yello.server.domain.event.entity.EventRewardRandomType.RANDOM;
import static com.yello.server.domain.event.entity.EventType.ADMOB;
import static com.yello.server.domain.event.entity.EventType.LUNCH_EVENT;
import static com.yello.server.global.common.util.ConstantUtil.GlobalZoneId;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.domain.authorization.filter.JwtExceptionFilter;
import com.yello.server.domain.authorization.filter.JwtFilter;
import com.yello.server.domain.event.controller.EventController;
import com.yello.server.domain.event.dto.response.EventResponse;
import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventReward;
import com.yello.server.domain.event.entity.EventRewardMapping;
import com.yello.server.domain.event.entity.EventTime;
import com.yello.server.domain.event.service.EventService;
import com.yello.server.global.exception.ControllerExceptionAdvice;
import com.yello.server.util.TestDataEntityUtil;
import com.yello.server.util.TestDataUtil;
import com.yello.server.util.WithAccessTokenUser;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureRestDocs
@WebMvcTest(
    controllers = {
        EventController.class
    },
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtExceptionFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ControllerExceptionAdvice.class)
    })
@WithAccessTokenUser
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("Event 컨트롤러에서")
class EventControllerTest {

    final OperationPreprocessor[] excludeRequestHeaders = new OperationPreprocessor[]{
        prettyPrint(),
        modifyHeaders().remove("X-CSRF-TOKEN"),
        modifyHeaders().remove(HttpHeaders.HOST)
    };

    final OperationPreprocessor[] excludeResponseHeaders = new OperationPreprocessor[]{
        prettyPrint(),
        modifyHeaders().remove("X-Content-Type-Options"),
        modifyHeaders().remove("X-XSS-Protection"),
        modifyHeaders().remove("X-Frame-Options"),
        modifyHeaders().remove(HttpHeaders.CACHE_CONTROL),
        modifyHeaders().remove(HttpHeaders.PRAGMA),
        modifyHeaders().remove(HttpHeaders.EXPIRES),
        modifyHeaders().remove(HttpHeaders.CONTENT_LENGTH),
    };

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    private TestDataUtil testDataUtil = new TestDataEntityUtil();

    /**
     * TODO Event* TestUtil 작성 및 연결 필요
     */
    @Test
    void 이벤트_전체_조회에_성공합니다1() throws Exception {
        final EventReward ticket = EventReward.builder()
            .tag("POINT")
            .maxRewardValue(200L)
            .minRewardValue(10L)
            .title("최대 200P")
            .image("https://storage.googleapis.com/yelloworld/image/coin-stack.svg")
            .build();
        final EventReward point = EventReward.builder()
            .tag("TICKET")
            .maxRewardValue(1L)
            .minRewardValue(1L)
            .title("열람권 1개")
            .image("https://storage.googleapis.com/yelloworld/image/key.svg")
            .build();

        final Event lunchEvent = Event.builder()
            .tag(LUNCH_EVENT)
            .startDate(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, GlobalZoneId))
            .endDate(ZonedDateTime.of(2024, 12, 31, 0, 0, 0, 0, GlobalZoneId))
            .title("점심 시간 깜짝 선물!")
            .subTitle("평일 12-14시 최대 1회까지 참여 가능")
            .animation("[{\"v\": \"로티1\"},{\"v\": \"로티2\"}]")
            .build();

        final EventTime eventTime1 = EventTime.builder()
            .event(lunchEvent)
            .rewardCount(1L)
            .startTime(OffsetTime.of(12, 0, 0, 0, ZoneOffset.of("+09:00")))
            .endTime(OffsetTime.of(14, 0, 0, 0, ZoneOffset.of("+09:00")))
            .build();

        final List<EventRewardMapping> rewardList1 = List.of(
            EventRewardMapping.builder()
                .eventTime(eventTime1)
                .eventReward(ticket)
                .eventRewardProbability(10)
                .randomTag(FIXED)
                .build(),
            EventRewardMapping.builder()
                .eventTime(eventTime1)
                .eventReward(point)
                .eventRewardProbability(90)
                .randomTag(RANDOM)
                .build()
        );

        final EventReward admobPoint = EventReward.builder()
            .tag("ADMOB_POINT")
            .maxRewardValue(50L)
            .minRewardValue(50L)
            .title("광고 보고 포인트 얻기")
            .image("https://storage.googleapis.com/yelloworld/image/coin-stack.svg")
            .build();
        final Event admobEvent = Event.builder()
            .tag(ADMOB)
            .startDate(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, GlobalZoneId))
            .endDate(ZonedDateTime.of(2024, 12, 31, 0, 0, 0, 0, GlobalZoneId))
            .title("ADMOB 광고입니다.")
            .subTitle("ADMOB 광고는 영구 사용 가능 설정입니다")
            .animation("[]")
            .build();
        final EventTime eventTime3 = EventTime.builder()
            .event(admobEvent)
            .rewardCount(1L)
            .startTime(OffsetTime.of(0, 0, 0, 0, ZoneOffset.of("+09:00")))
            .endTime(OffsetTime.of(23, 59, 59, 999999, ZoneOffset.of("+09:00")))
            .build();
        final List<EventRewardMapping> rewardList3 = List.of(
            EventRewardMapping.builder()
                .eventTime(eventTime3)
                .eventReward(admobPoint)
                .eventRewardProbability(100)
                .randomTag(FIXED)
                .build()
        );

        final EventResponse response1 = EventResponse.of(lunchEvent, eventTime1, rewardList1);
        final EventResponse response3 = EventResponse.of(admobEvent, eventTime3, rewardList3);
        final List<EventResponse> result = List.of(response1, response3);

        given(eventService.getEvents(anyLong()))
            .willReturn(result);

        // when

        // then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .get("/api/v1/event")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
            )
            .andDo(print())
            .andDo(document("api/v1/event/1",
                Preprocessors.preprocessRequest(excludeRequestHeaders),
                Preprocessors.preprocessResponse(excludeResponseHeaders)
            ))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 이벤트_전체_조회에_성공합니다2() throws Exception {
        final EventReward ticket = EventReward.builder()
            .tag("POINT")
            .maxRewardValue(200L)
            .minRewardValue(10L)
            .title("최대 200P")
            .image("https://storage.googleapis.com/yelloworld/image/coin-stack.svg")
            .build();
        final EventReward point = EventReward.builder()
            .tag("TICKET")
            .maxRewardValue(1L)
            .minRewardValue(1L)
            .title("열람권 1개")
            .image("https://storage.googleapis.com/yelloworld/image/key.svg")
            .build();

        final Event lunchEvent = Event.builder()
            .tag(LUNCH_EVENT)
            .startDate(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, GlobalZoneId))
            .endDate(ZonedDateTime.of(2024, 12, 31, 0, 0, 0, 0, GlobalZoneId))
            .title("점심 시간 깜짝 선물!")
            .subTitle("평일 12-14시 최대 1회까지 참여 가능")
            .animation("[{\"v\": \"로티1\"},{\"v\": \"로티2\"}]")
            .build();

        final EventTime eventTime2 = EventTime.builder()
            .event(lunchEvent)
            .rewardCount(1L)
            .startTime(OffsetTime.of(22, 0, 0, 0, ZoneOffset.of("+09:00")))
            .endTime(OffsetTime.of(0, 0, 0, 0, ZoneOffset.of("+09:00")))
            .build();

        final List<EventRewardMapping> rewardList2 = List.of(
            EventRewardMapping.builder()
                .eventTime(eventTime2)
                .eventReward(ticket)
                .eventRewardProbability(40)
                .randomTag(FIXED)
                .build(),
            EventRewardMapping.builder()
                .eventTime(eventTime2)
                .eventReward(point)
                .eventRewardProbability(60)
                .randomTag(RANDOM)
                .build()
        );

        final EventReward admobPoint = EventReward.builder()
            .tag("ADMOB_POINT")
            .maxRewardValue(50L)
            .minRewardValue(50L)
            .title("광고 보고 포인트 얻기")
            .image("https://storage.googleapis.com/yelloworld/image/coin-stack.svg")
            .build();
        final Event admobEvent = Event.builder()
            .tag(ADMOB)
            .startDate(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, GlobalZoneId))
            .endDate(ZonedDateTime.of(2024, 12, 31, 0, 0, 0, 0, GlobalZoneId))
            .title("ADMOB 광고입니다.")
            .subTitle("ADMOB 광고는 영구 사용 가능 설정입니다")
            .animation("[]")
            .build();
        final EventTime eventTime3 = EventTime.builder()
            .event(admobEvent)
            .rewardCount(1L)
            .startTime(OffsetTime.of(0, 0, 0, 0, ZoneOffset.of("+09:00")))
            .endTime(OffsetTime.of(23, 59, 59, 999999, ZoneOffset.of("+09:00")))
            .build();
        final List<EventRewardMapping> rewardList3 = List.of(
            EventRewardMapping.builder()
                .eventTime(eventTime3)
                .eventReward(admobPoint)
                .eventRewardProbability(100)
                .randomTag(FIXED)
                .build()
        );

        final EventResponse response2 = EventResponse.of(lunchEvent, eventTime2, rewardList2);
        final EventResponse response3 = EventResponse.of(admobEvent, eventTime3, rewardList3);
        final List<EventResponse> result = List.of(response2, response3);

        given(eventService.getEvents(anyLong()))
            .willReturn(result);

        // when

        // then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .get("/api/v1/event")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
            )
            .andDo(print())
            .andDo(document("api/v1/event/2",
                Preprocessors.preprocessRequest(excludeRequestHeaders),
                Preprocessors.preprocessResponse(excludeResponseHeaders)
            ))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 이벤트_전체_조회에_성공합니다3() throws Exception {
        final Event lunchEvent = Event.builder()
            .tag(LUNCH_EVENT)
            .startDate(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, GlobalZoneId))
            .endDate(ZonedDateTime.of(2024, 12, 31, 0, 0, 0, 0, GlobalZoneId))
            .title("점심 시간 깜짝 선물!")
            .subTitle("평일 12-14시 최대 1회까지 참여 가능")
            .animation("[{\"v\": \"로티1\"},{\"v\": \"로티2\"}]")
            .build();

        final EventReward admobPoint = EventReward.builder()
            .tag("ADMOB_POINT")
            .maxRewardValue(50L)
            .minRewardValue(50L)
            .title("광고 보고 포인트 얻기")
            .image("https://storage.googleapis.com/yelloworld/image/coin-stack.svg")
            .build();
        final Event admobEvent = Event.builder()
            .tag(ADMOB)
            .startDate(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, GlobalZoneId))
            .endDate(ZonedDateTime.of(2024, 12, 31, 0, 0, 0, 0, GlobalZoneId))
            .title("ADMOB 광고입니다.")
            .subTitle("ADMOB 광고는 영구 사용 가능 설정입니다")
            .animation("[]")
            .build();
        final EventTime eventTime3 = EventTime.builder()
            .event(admobEvent)
            .rewardCount(1L)
            .startTime(OffsetTime.of(0, 0, 0, 0, ZoneOffset.of("+09:00")))
            .endTime(OffsetTime.of(23, 59, 59, 999999, ZoneOffset.of("+09:00")))
            .build();
        final List<EventRewardMapping> rewardList3 = List.of(
            EventRewardMapping.builder()
                .eventTime(eventTime3)
                .eventReward(admobPoint)
                .eventRewardProbability(100)
                .randomTag(FIXED)
                .build()
        );

        final EventResponse response2 = EventResponse.of(lunchEvent, null, null);
        final EventResponse response3 = EventResponse.of(admobEvent, eventTime3, rewardList3);
        final List<EventResponse> result = List.of(response2, response3);

        given(eventService.getEvents(anyLong()))
            .willReturn(result);

        // when

        // then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .get("/api/v1/event")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
            )
            .andDo(print())
            .andDo(document("api/v1/event/3",
                Preprocessors.preprocessRequest(excludeRequestHeaders),
                Preprocessors.preprocessResponse(excludeResponseHeaders)
            ))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
