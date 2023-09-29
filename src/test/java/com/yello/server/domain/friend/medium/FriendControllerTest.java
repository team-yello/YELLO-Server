package com.yello.server.domain.friend.medium;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yello.server.domain.authorization.filter.JwtExceptionFilter;
import com.yello.server.domain.authorization.filter.JwtFilter;
import com.yello.server.domain.friend.controller.FriendController;
import com.yello.server.domain.friend.dto.request.KakaoRecommendRequest;
import com.yello.server.domain.friend.dto.response.FriendResponse;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.dto.response.FriendsResponse;
import com.yello.server.domain.friend.dto.response.RecommendFriendResponse;
import com.yello.server.domain.friend.dto.response.SearchFriendResponse;
import com.yello.server.domain.friend.dto.response.SearchFriendVO;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.service.FriendService;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.exception.ControllerExceptionAdvice;
import com.yello.server.infrastructure.firebase.service.NotificationService;
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

@WebMvcTest(
    controllers = FriendController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtExceptionFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ControllerExceptionAdvice.class)
    })
@AutoConfigureRestDocs
@WithAccessTokenUser
@DisplayName("FriendController 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
class FriendControllerTest {

    final String[] excludeRequestHeaders = {"X-CSRF-TOKEN", "Host"};
    final String[] excludeResponseHeaders = {"X-Content-Type-Options", "X-XSS-Protection", "Cache-Control", "Pragma",
        "Expires", "X-Frame-Options", "Content-Length"};

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private FriendService friendService;

    @MockBean
    private NotificationService notificationService;

    private TestDataUtil testDataUtil = new TestDataEntityUtil();
    private User user;
    private User target;

    @BeforeEach
    void init() {
        user = testDataUtil.generateUser(1L, 1L, UserGroupType.UNIVERSITY);
        target = testDataUtil.generateUser(2L, 1L, UserGroupType.UNIVERSITY);
    }

    @Test
    void 친구_추가에_성공합니다() throws Exception {
        // given
        given(friendService.addFriend(anyLong(), anyLong()))
            .willReturn(null);

        doNothing()
            .when(notificationService)
            .sendFriendNotification(any(Friend.class));

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/friend/{targetId}", 1)
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token"))
            .andDo(print())
            .andDo(document("api/v1/friend/addFriend",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                pathParameters(parameterWithName("targetId").description("친구 신청할 상대 유저의 아이디 값")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 내_친구_전체_조회에_성공합니다() throws Exception {
        // given
        final UserResponse userResponse = UserResponse.of(user, 0, 0);
        final FriendsResponse friendsResponse = FriendsResponse.of(0L, Arrays.asList(userResponse));

        given(friendService.findAllFriends(any(Pageable.class), anyLong()))
            .willReturn(friendsResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/friend")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .param("page", "0"))
            .andDo(print())
            .andDo(document("api/v1/friend/findAllFriend",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                requestParameters(parameterWithName("page").description("페이지네이션 페이지 번호")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 친구_투표_조회에_성공합니다() throws Exception {
        // given
        final Friend friend = testDataUtil.generateFriend(user, target);
        final FriendShuffleResponse friendShuffleResponse = FriendShuffleResponse.of(friend);

        given(friendService.findShuffledFriend(anyLong()))
            .willReturn(Arrays.asList(friendShuffleResponse));

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/friend/shuffle")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token"))
            .andDo(print())
            .andDo(document("api/v1/friend/findShuffledFriend",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 그룹_추천_친구_조회에_성공합니다() throws Exception {
        // given
        final FriendResponse friendResponse = FriendResponse.of(user);
        final RecommendFriendResponse recommendFriendResponse = RecommendFriendResponse.of(0,
            Arrays.asList(friendResponse));

        given(friendService.findAllRecommendSchoolFriends(any(Pageable.class), anyLong()))
            .willReturn(recommendFriendResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/friend/recommend/school")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .param("page", "0"))
            .andDo(print())
            .andDo(document("api/v1/friend/findAllRecommendSchoolFriends",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                requestParameters(parameterWithName("page").description("페이지네이션 페이지 번호")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 카카오_추천_친구_조회에_성공합니다() throws Exception {
        // given
        final KakaoRecommendRequest kakaoRecommendRequest = KakaoRecommendRequest.builder()
            .friendKakaoId(new String[]{"testKakaoId"})
            .build();
        final FriendResponse friendResponse = FriendResponse.of(user);
        final RecommendFriendResponse recommendFriendResponse = RecommendFriendResponse.of(0,
            Arrays.asList(friendResponse));

        given(friendService.findAllRecommendKakaoFriends(any(Pageable.class), anyLong(), eq(kakaoRecommendRequest)))
            .willReturn(recommendFriendResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/friend/recommend/kakao")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kakaoRecommendRequest))
                .param("page", "0"))
            .andDo(print())
            .andDo(document("api/v1/friend/findAllRecommendKakaoFriends",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                requestParameters(parameterWithName("page").description("페이지네이션 페이지 번호")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 친구_삭제에_성공합니다() throws Exception {
        // given
        doNothing()
            .when(friendService)
            .deleteFriend(anyLong(), anyLong());

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/friend/{targetId}", 1)
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
            )
            .andDo(print())
            .andDo(document("api/v1/friend/deleteFriend",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                pathParameters(parameterWithName("targetId").description("삭제할 상대 유저의 아이디 값")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 친구_검색에_성공합니다() throws Exception {
        // given
        final SearchFriendVO searchFriendVO = SearchFriendVO.of(user, false);
        final SearchFriendResponse searchFriendResponse = SearchFriendResponse.of(0, Arrays.asList(searchFriendVO));

        given(friendService.searchFriend(anyLong(), any(Pageable.class), anyString()))
            .willReturn(searchFriendResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/friend/search")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .param("page", "0")
                .param("keyword", "keyword here")
            )
            .andDo(print())
            .andDo(document("api/v1/friend/searchFriend",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                requestParameters(
                    parameterWithName("page").description("페이지네이션 페이지 번호"),
                    parameterWithName("keyword").description("검색할 쿼리")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
