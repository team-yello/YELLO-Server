package com.yello.server.domain.authorization.medium;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.domain.authorization.controller.AuthController;
import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.request.OnBoardingFriendRequest;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.authorization.dto.response.DepartmentSearchResponse;
import com.yello.server.domain.authorization.dto.response.GroupNameSearchResponse;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;
import com.yello.server.domain.authorization.dto.response.OnBoardingFriendResponse;
import com.yello.server.domain.authorization.dto.response.SignUpResponse;
import com.yello.server.domain.authorization.filter.JwtExceptionFilter;
import com.yello.server.domain.authorization.filter.JwtFilter;
import com.yello.server.domain.authorization.service.AuthService;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.exception.ControllerExceptionAdvice;
import com.yello.server.util.TestDataEntityUtil;
import com.yello.server.util.TestDataUtil;
import com.yello.server.util.WithAccessTokenUser;
import java.util.Arrays;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureRestDocs
@WebMvcTest(
    controllers = AuthController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtExceptionFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ControllerExceptionAdvice.class)
    })
@WithAccessTokenUser
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("Auth 컨트롤러에서")
class AuthControllerTest {

    final String[] excludeRequestHeaders = {"X-CSRF-TOKEN", "Host"};
    final String[] excludeResponseHeaders = {"X-Content-Type-Options", "X-XSS-Protection", "Cache-Control", "Pragma",
        "Expires", "X-Frame-Options", "Content-Length"};

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private TestDataUtil testDataUtil = new TestDataEntityUtil();
    private User user;
    private User target;

    @BeforeEach
    void init() {
        user = testDataUtil.generateUser(1L, 1L);
        target = testDataUtil.generateUser(2L, 1L);
    }

    @Test
    void 소셜_로그인에_성공합니다() throws Exception {
        // given
        final OAuthRequest oAuthRequest = OAuthRequest.builder()
            .accessToken("accessToken")
            .deviceToken("deviceToken")
            .social("social")
            .build();

        final ServiceTokenVO serviceTokenVO = ServiceTokenVO.of(
            "serviceAccessToken",
            "serviceRefreshToken"
        );

        final OAuthResponse oAuthResponse = OAuthResponse.of(false, serviceTokenVO);

        given(authService.oauthLogin(oAuthRequest))
            .willReturn(oAuthResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/oauth")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oAuthRequest)))
            .andDo(print())
            .andDo(document("api/v1/auth/oauthLogin",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 옐로_아이디_중복_확인에_성공합니다() throws Exception {
        // given
        given(authService.isYelloIdDuplicated(anyString()))
            .willReturn(false);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/auth/valid")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .param("yelloId", "yelloId here"))
            .andDo(print())
            .andDo(document("api/v1/auth/getYelloIdValidation",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                requestParameters(parameterWithName("yelloId").description("중복 체크할 yelloId")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 회원_가입에_성공합니다() throws Exception {
        // given
        final SignUpRequest signUpRequest = SignUpRequest.builder()
            .social(Social.KAKAO)
            .uuid("uuid")
            .deviceToken("deviceToken")
            .email("email@emall.com")
            .profileImage("profileImage")
            .groupId(1L)
            .groupAdmissionYear(20)
            .name("name")
            .yelloId("yelloId")
            .gender(Gender.MALE)
            .friends(Arrays.asList(1L))
            .recommendId("recommendId")
            .build();

        final ServiceTokenVO serviceTokenVO = ServiceTokenVO.of(
            "serviceAccessToken",
            "serviceRefreshToken"
        );

        final SignUpResponse signUpResponse = SignUpResponse.of("yelloId", serviceTokenVO);

        given(authService.signUp(signUpRequest))
            .willReturn(signUpResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/signup")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
            .andDo(print())
            .andDo(document("api/v1/auth/postSignUp",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 가입한_친구_불러오기에_성공합니다() throws Exception {
        // given
        final OnBoardingFriendRequest onBoardingFriendRequest = OnBoardingFriendRequest.builder()
            .friendKakaoId(Arrays.asList("friendKakaoId"))
            .build();

        final OnBoardingFriendResponse onBoardingFriendResponse = OnBoardingFriendResponse.of(0, Arrays.asList(user));

        given(authService.findOnBoardingFriends(any(OnBoardingFriendRequest.class), any(Pageable.class)))
            .willReturn(onBoardingFriendResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/friend")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(onBoardingFriendRequest))
                .param("page", "0"))
            .andDo(print())
            .andDo(document("api/v1/auth/findOnBoardingFriends",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                requestParameters(parameterWithName("page").description("페이지네이션 페이지 번호")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 대학교_이름_검색에_성공합니다() throws Exception {
        // given
        final GroupNameSearchResponse groupNameSearchResponse = GroupNameSearchResponse.of(
            0,
            Arrays.asList("groupName")
        );

        given(authService.findSchoolsByKeyword(anyString(), any(Pageable.class)))
            .willReturn(groupNameSearchResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/auth/school")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .param("page", "0")
                .param("keyword", "keyword here")
            )
            .andDo(print())
            .andDo(document("api/v1/auth/findSchoolsByKeyword",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                requestParameters(
                    parameterWithName("page").description("페이지네이션 페이지 번호"),
                    parameterWithName("keyword").description("검색할 쿼리")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 학과_이름_검색에_성공합니다() throws Exception {
        // given
        final DepartmentSearchResponse departmentSearchResponse = DepartmentSearchResponse.of(
            0,
            Arrays.asList(testDataUtil.generateSchool(1L))
        );

        given(authService.findDepartmentsByKeyword(anyString(), anyString(), any(Pageable.class)))
            .willReturn(departmentSearchResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/auth/school/department")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .param("page", "0")
                .param("school", "school name here")
                .param("keyword", "keyword here")
            )
            .andDo(print())
            .andDo(document("api/v1/auth/findDepartmentsByKeyword",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                requestParameters(
                    parameterWithName("page").description("페이지네이션 페이지 번호"),
                    parameterWithName("school").description("학교 이름"),
                    parameterWithName("keyword").description("검색할 쿼리")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 토큰_재발급에_성공합니다() throws Exception {
        // given
        final ServiceTokenVO serviceTokenVO = ServiceTokenVO.of(
            "new accessToken",
            "new refreshToken"
        );

        given(authService.reIssueToken(any(ServiceTokenVO.class)))
            .willReturn(serviceTokenVO);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/token/issue")
                .with(csrf().asHeader())
                .header("X-ACCESS-AUTH", "Bearer your-access-token")
                .header("X-REFRESH-AUTH", "Bearer your-refresh-token")
            )
            .andDo(print())
            .andDo(document("api/v1/auth/reIssueToken",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
