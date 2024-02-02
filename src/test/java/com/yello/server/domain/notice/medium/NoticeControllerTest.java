package com.yello.server.domain.notice.medium;


import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.domain.authorization.filter.JwtExceptionFilter;
import com.yello.server.domain.authorization.filter.JwtFilter;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.notice.controller.NoticeController;
import com.yello.server.domain.notice.dto.NoticeDataResponse;
import com.yello.server.domain.notice.entity.Notice;
import com.yello.server.domain.notice.entity.NoticeType;
import com.yello.server.domain.notice.service.NoticeService;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.exception.ControllerExceptionAdvice;
import com.yello.server.util.TestDataEntityUtil;
import com.yello.server.util.TestDataUtil;
import com.yello.server.util.WithAccessTokenUser;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureRestDocs
@WebMvcTest(
    controllers = NoticeController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtExceptionFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ControllerExceptionAdvice.class)
    })
@WithAccessTokenUser
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("Notice 컨트롤러에서")
public class NoticeControllerTest {

    final String[] excludeRequestHeaders = {"X-CSRF-TOKEN", "Host"};
    final String[] excludeResponseHeaders =
        {"X-Content-Type-Options", "X-XSS-Protection", "Cache-Control", "Pragma",
            "Expires", "X-Frame-Options", "Content-Length"};

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NoticeService noticeService;

    private TestDataUtil testDataUtil = new TestDataEntityUtil();
    private User user;
    private Notice notice;
    private ZonedDateTime now;

    @BeforeEach
    void init() {
        ZonedDateTime fixedDateTime = ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneId.of("Asia/Seoul"));
        Clock fixedClock = Clock.fixed(fixedDateTime.toInstant(), fixedDateTime.getZone());
        now = ZonedDateTime.now(fixedClock);

        final UserGroup userGroup = testDataUtil.generateGroup(1L, UserGroupType.UNIVERSITY);
        user = testDataUtil.generateUser(1L, userGroup);
        notice = testDataUtil.generateNotice(1L, NoticeType.NOTICE, now);
    }

    @Test
    void 공지_조회하기_검증에_성공합니다() throws Exception {
        // given
        final NoticeDataResponse noticeDataResponse = NoticeDataResponse.of(notice, false);
        NoticeType tag = NoticeType.NOTICE;
        given(noticeService.findNotice(anyLong(), String.valueOf(eq(tag))))
            .willReturn(noticeDataResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders
                .get("/api/v1/notice/{tag}", "NOTICE")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token"))
            .andDo(print())
            .andDo(document("api/v1/notice",
                Preprocessors.preprocessRequest(prettyPrint(),
                    removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(),
                    removeHeaders(excludeResponseHeaders)),
                pathParameters(parameterWithName("tag").description("공지의 종류")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
