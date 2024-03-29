package com.yello.server.domain.user.medium;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.domain.authorization.filter.JwtExceptionFilter;
import com.yello.server.domain.authorization.filter.JwtFilter;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.user.controller.UserController;
import com.yello.server.domain.user.dto.request.UserDataUpdateRequest;
import com.yello.server.domain.user.dto.request.UserDeleteReasonRequest;
import com.yello.server.domain.user.dto.request.UserDeviceTokenRequest;
import com.yello.server.domain.user.dto.request.UserUpdateRequest;
import com.yello.server.domain.user.dto.response.UserDataResponse;
import com.yello.server.domain.user.dto.response.UserDetailResponse;
import com.yello.server.domain.user.dto.response.UserDetailV2Response;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.dto.response.UserSubscribeDetailResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserDataType;
import com.yello.server.domain.user.service.UserService;
import com.yello.server.global.common.dto.EmptyObject;
import com.yello.server.global.exception.ControllerExceptionAdvice;
import com.yello.server.util.TestDataEntityUtil;
import com.yello.server.util.TestDataUtil;
import com.yello.server.util.WithAccessTokenUser;
import java.time.LocalDateTime;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureRestDocs
@WebMvcTest(
    controllers = {
        UserController.class
    },
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtExceptionFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ControllerExceptionAdvice.class)
    })
@WithAccessTokenUser
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("User 컨트롤러에서")
class UserControllerTest {

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
    private UserService userService;

    private TestDataUtil testDataUtil = new TestDataEntityUtil();
    private User user;

    @BeforeEach
    void init() {
        final UserGroup userGroup = testDataUtil.generateGroup(1L, UserGroupType.UNIVERSITY);
        user = testDataUtil.generateUser(1L, userGroup);
    }

    @Test
    void 내_정보_조회에_성공합니다() throws Exception {
        // given
        final UserDetailResponse userDetailResponse = UserDetailResponse.builder()
            .userId(1L)
            .name("test")
            .group("group")
            .profileImageUrl("profile")
            .yelloId("yelloId")
            .yelloCount(0)
            .friendCount(0)
            .point(200)
            .build();

        given(userService.findMyProfile(anyLong()))
            .willReturn(userDetailResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
            )
            .andDo(print())
            .andDo(document("api/v1/user/findUser",
                Preprocessors.preprocessRequest(excludeRequestHeaders),
                Preprocessors.preprocessResponse(excludeResponseHeaders)
            ))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 내_정보_조회_V2에_성공합니다() throws Exception {
        // given
        UserDetailV2Response response = UserDetailV2Response.of(user, user.getGroup(), 100, 200);

        given(userService.getUserDetailV2(anyLong()))
            .willReturn(response);

        // when

        // then
        mockMvc
            .perform(
                RestDocumentationRequestBuilders
                    .get("/api/v2/user")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
            )
            .andDo(print())
            .andDo(document("api/v2/user",
                Preprocessors.preprocessRequest(excludeRequestHeaders),
                Preprocessors.preprocessResponse(excludeResponseHeaders)
            ))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 다른_유저_정보_조회에_성공합니다() throws Exception {
        // given
        final Long userId = 2L;
        final UserResponse userResponse = UserResponse.builder()
            .userId(userId)
            .name("test")
            .group("group")
            .profileImageUrl("profile")
            .yelloId("yelloId")
            .yelloCount(0)
            .friendCount(0)
            .build();

        given(userService.findUserById(anyLong()))
            .willReturn(userResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/user/{userId}", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
            )
            .andDo(print())
            .andDo(document("api/v1/user/findUserById",
                Preprocessors.preprocessRequest(excludeRequestHeaders),
                Preprocessors.preprocessResponse(excludeResponseHeaders),
                pathParameters(parameterWithName("userId").description("유저 아이디 값")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 디바이스_토큰_수정에_성공합니다() throws Exception {
        // given
        final EmptyObject emptyObject = EmptyObject.builder().build();
        final UserDeviceTokenRequest userDeviceTokenRequest = UserDeviceTokenRequest.builder()
            .deviceToken("testDeviceToken")
            .build();

        given(userService.updateUserDeviceToken(any(User.class), eq(userDeviceTokenRequest)))
            .willReturn(emptyObject);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/user/device")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDeviceTokenRequest)))
            .andDo(print())
            .andDo(document("api/v1/user/updateUserDeviceToken",
                Preprocessors.preprocessRequest(excludeRequestHeaders),
                Preprocessors.preprocessResponse(excludeResponseHeaders)
            ))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 유저_탈퇴에_성공합니다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/user")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
            )
            .andDo(print())
            .andDo(document("api/v1/user/deleteUser",
                Preprocessors.preprocessRequest(excludeRequestHeaders),
                Preprocessors.preprocessResponse(excludeResponseHeaders)
            ))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 유저_구독_정보_조회에_성공합니다() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        UserSubscribeDetailResponse userSubscribeDetailResponse =
            UserSubscribeDetailResponse.of(testDataUtil.generatePurchase(1L, user, now));
        // when
        given(userService.getUserSubscribe(anyLong()))
            .willReturn(userSubscribeDetailResponse);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders
                .get("/api/v1/user/subscribe")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
            )
            .andDo(print())
            .andDo(document("api/v1/user/subscribe",
                Preprocessors.preprocessRequest(excludeRequestHeaders),
                Preprocessors.preprocessResponse(excludeResponseHeaders)
            ))
            .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void 유저_탈퇴_v2_성공합니다() throws Exception {
        // given
        final UserDeleteReasonRequest request =
            UserDeleteReasonRequest.builder().value("오류가 많아서").build();

        doNothing()
            .when(userService)
            .deleteUserWithReason(anyLong(), eq(request));
        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v2/user")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andDo(document("api/v2/user/deleteUser",
                Preprocessors.preprocessRequest(excludeRequestHeaders),
                Preprocessors.preprocessResponse(excludeResponseHeaders)
            ))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 유저_정보_수정에_성공합니다() throws Exception {
        // given
        final UserUpdateRequest request =
            UserUpdateRequest.builder()
                .name("after")
                .email("afterupdate@yello.com")
                .yelloId("afterupdate")
                .gender("M")
                .profileImageUrl("https://after.com")
                .groupId(1L)
                .groupAdmissionYear(24)
                .build();

        doNothing()
            .when(userService)
            .updateUserProfile(any(Long.class), eq(request));
        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/user")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andDo(document("api/v1/user/update",
                Preprocessors.preprocessRequest(excludeRequestHeaders),
                Preprocessors.preprocessResponse(excludeResponseHeaders)
            ))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 유저_데이터_조회에_성공합니다1() throws Exception {
        // given
        final UserDataResponse response =
            UserDataResponse.builder()
                .tag(UserDataType.ACCOUNT_UPDATED_AT.getInitial())
                .value("false|2024-01-31|2024-01-31")
                .build();

        given(userService.readUserData(any(Long.class), any(UserDataType.class)))
            .willReturn(response);
        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/user/data/{tag}",
                    UserDataType.ACCOUNT_UPDATED_AT.getInitial())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andDo(document("api/v1/user/data/read/1",
                Preprocessors.preprocessRequest(excludeRequestHeaders),
                Preprocessors.preprocessResponse(excludeResponseHeaders)
            ))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 유저_데이터_조회에_성공합니다2() throws Exception {
        // given
        final UserDataResponse response =
            UserDataResponse.builder()
                .tag(UserDataType.ACCOUNT_UPDATED_AT.getInitial())
                .value("true|null|2024-01-31")
                .build();

        given(userService.readUserData(any(Long.class), any(UserDataType.class)))
            .willReturn(response);
        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/user/data/{tag}",
                    UserDataType.ACCOUNT_UPDATED_AT.getInitial())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andDo(document("api/v1/user/data/read/2",
                Preprocessors.preprocessRequest(excludeRequestHeaders),
                Preprocessors.preprocessResponse(excludeResponseHeaders)
            ))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 유저_데이터_수정_및_저장에_성공합니다() throws Exception {
        // given
        final UserDataUpdateRequest request =
            UserDataUpdateRequest.builder()
                .value("바꿀 값임요")
                .build();

        doNothing()
            .when(userService)
            .updateUserData(any(Long.class), any(UserDataType.class), eq(request));
        // when

        // then
        mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/user/data/{tag}", UserDataType.WITHDRAW_REASON.getInitial())
                    .with(csrf().asHeader())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andDo(document("api/v1/user/data/update",
                Preprocessors.preprocessRequest(excludeRequestHeaders),
                Preprocessors.preprocessResponse(excludeResponseHeaders)
            ))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
