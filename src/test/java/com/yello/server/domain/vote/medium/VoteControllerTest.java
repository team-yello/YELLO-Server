package com.yello.server.domain.vote.medium;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.keyword.dto.response.KeywordCheckResponse;
import com.yello.server.domain.question.dto.response.QuestionForVoteResponse;
import com.yello.server.domain.question.dto.response.QuestionVO;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.vote.controller.VoteController;
import com.yello.server.domain.vote.dto.request.CreateVoteRequest;
import com.yello.server.domain.vote.dto.request.VoteAnswer;
import com.yello.server.domain.vote.dto.response.RevealFullNameResponse;
import com.yello.server.domain.vote.dto.response.RevealNameResponse;
import com.yello.server.domain.vote.dto.response.VoteAvailableResponse;
import com.yello.server.domain.vote.dto.response.VoteCountVO;
import com.yello.server.domain.vote.dto.response.VoteCreateResponse;
import com.yello.server.domain.vote.dto.response.VoteCreateVO;
import com.yello.server.domain.vote.dto.response.VoteDetailResponse;
import com.yello.server.domain.vote.dto.response.VoteFriendResponse;
import com.yello.server.domain.vote.dto.response.VoteFriendVO;
import com.yello.server.domain.vote.dto.response.VoteListResponse;
import com.yello.server.domain.vote.dto.response.VoteResponse;
import com.yello.server.domain.vote.dto.response.VoteUnreadCountResponse;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.service.VoteService;
import com.yello.server.global.exception.ControllerExceptionAdvice;
import com.yello.server.infrastructure.firebase.service.NotificationService;
import com.yello.server.util.TestDataEntityUtil;
import com.yello.server.util.TestDataUtil;
import com.yello.server.util.WithAccessTokenUser;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
    controllers = VoteController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtExceptionFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ControllerExceptionAdvice.class)
    })
@WithAccessTokenUser
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("Vote 컨트롤러에서")
class VoteControllerTest {

    final String[] excludeRequestHeaders = {"X-CSRF-TOKEN", "Host"};
    final String[] excludeResponseHeaders = {"X-Content-Type-Options", "X-XSS-Protection", "Cache-Control", "Pragma",
        "Expires", "X-Frame-Options", "Content-Length"};

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VoteService voteService;

    @MockBean
    private NotificationService notificationService;

    private TestDataUtil testDataUtil = new TestDataEntityUtil();
    private User user;
    private User target;

    @BeforeEach
    void init() {
        user = testDataUtil.generateUser(1L, 1L);
        target = testDataUtil.generateUser(2L, 1L);
    }

    @Test
    void 내_투표_전체_조회에_성공합니다() throws Exception {
        // given
        final Vote vote = testDataUtil.generateVote(1L, user, target, testDataUtil.generateQuestion(1L));
        final VoteResponse voteResponse = VoteResponse.of(vote);
        final VoteCountVO voteCountVO = VoteCountVO.of(1, 0, 0, 0, 0);
        final VoteListResponse voteListResponse = VoteListResponse.of(voteCountVO, Arrays.asList(voteResponse), user);

        given(voteService.findAllVotes(anyLong(), any(Pageable.class)))
            .willReturn(voteListResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/vote")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .param("page", "0"))
            .andDo(print())
            .andDo(document("api/v1/vote/findAllMyVotes",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                requestParameters(parameterWithName("page").description("페이지네이션 페이지 번호")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 읽지_않은_쪽지_개수_조회에_성공합니다() throws Exception {
        // given
        final VoteUnreadCountResponse voteUnreadCountResponse = VoteUnreadCountResponse.of(1);

        given(voteService.getUnreadVoteCount(anyLong()))
            .willReturn(voteUnreadCountResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/vote/count")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
            )
            .andDo(print())
            .andDo(document("api/v1/vote/getUnreadVoteCount",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 친구_투표_조회에_성공합니다() throws Exception {
        // given
        final Vote vote = testDataUtil.generateVote(1L, user, target, testDataUtil.generateQuestion(1L));
        final VoteFriendVO voteFriendVO = VoteFriendVO.of(vote);
        final VoteFriendResponse voteFriendResponse = VoteFriendResponse.of(1, Arrays.asList(voteFriendVO));

        given(voteService.findAllFriendVotes(anyLong(), any(Pageable.class)))
            .willReturn(voteFriendResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/vote/friend")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .param("page", "0"))
            .andDo(print())
            .andDo(document("api/v1/vote/findAllFriendVotes",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                requestParameters(parameterWithName("page").description("페이지네이션 페이지 번호")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 투표_상세_조회에_성공합니다() throws Exception {
        // given
        final Vote vote = testDataUtil.generateVote(1L, user, target, testDataUtil.generateQuestion(1L));
        final VoteDetailResponse voteDetailResponse = VoteDetailResponse.of(vote, user);

        given(voteService.findVoteById(anyLong(), anyLong()))
            .willReturn(voteDetailResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/vote/{voteId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
            )
            .andDo(print())
            .andDo(document("api/v1/vote/findVoteById",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                pathParameters(parameterWithName("voteId").description("투표 아이디 값")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 키워드_확인에_성공합니다() throws Exception {
        // given
        final Vote vote = testDataUtil.generateVote(1L, user, target, testDataUtil.generateQuestion(1L));
        final KeywordCheckResponse keywordCheckResponse = KeywordCheckResponse.of(vote);

        given(voteService.checkKeyword(anyLong(), anyLong()))
            .willReturn(keywordCheckResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/vote/{voteId}/keyword", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .with(csrf().asHeader())
            )
            .andDo(print())
            .andDo(document("api/v1/vote/checkKeyword",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                pathParameters(parameterWithName("voteId").description("투표 아이디 값")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 투표_8개_조회에_성공합니다() throws Exception {
        // given
        final Friend friend = testDataUtil.generateFriend(user, target);
        final List<FriendShuffleResponse> friendShuffleResponses = Arrays.asList(FriendShuffleResponse.of(friend));
        final List<String> keywordList = Arrays.asList("A", "B", "C", "D");
        final QuestionVO questionVO = QuestionVO.of(testDataUtil.generateQuestion(1L));
        final QuestionForVoteResponse questionForVoteResponse = QuestionForVoteResponse.builder()
            .question(questionVO)
            .friendList(friendShuffleResponses)
            .keywordList(keywordList)
            .questionPoint(10)
            .subscribe("normal | active")
            .build();

        given(voteService.findVoteQuestionList(anyLong()))
            .willReturn(Arrays.asList(questionForVoteResponse));

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/vote/question")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
            )
            .andDo(print())
            .andDo(document("api/v1/vote/findVoteQuestions",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 투표_가능_여부_조회에_성공합니다() throws Exception {
        // given
        final Cooldown cooldown = Cooldown.of(user, UUID.randomUUID().toString(), LocalDateTime.now());
        final VoteAvailableResponse voteAvailableResponse = VoteAvailableResponse.of(user, cooldown);

        given(voteService.checkVoteAvailable(anyLong()))
            .willReturn(voteAvailableResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/vote/available")
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
            )
            .andDo(print())
            .andDo(document("api/v1/vote/checkVoteAvailable",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 투표_생성에_성공합니다() throws Exception {
        // given
        final Vote vote = testDataUtil.generateVote(1L, user, target, testDataUtil.generateQuestion(1L));
        final VoteCreateVO voteCreateVO = VoteCreateVO.of(10, Arrays.asList(vote));
        final VoteCreateResponse voteCreateResponse = VoteCreateResponse.of(10);
        final VoteAnswer voteAnswer = VoteAnswer.builder()
            .friendId(2L)
            .questionId(1L)
            .keywordName("키워드")
            .colorIndex(0)
            .build();

        final CreateVoteRequest createVoteRequest = CreateVoteRequest.builder()
            .voteAnswerList(Arrays.asList(voteAnswer))
            .totalPoint(10)
            .build();

        given(voteService.createVote(anyLong(), eq(createVoteRequest)))
            .willReturn(voteCreateVO);

        doNothing()
            .when(notificationService)
            .sendYelloNotification(any());

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/vote")
                .with(csrf().asHeader())
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createVoteRequest)))
            .andDo(print())
            .andDo(document("api/v1/vote/createVote",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 투표_이름_부분_조회에_성공합니다() throws Exception {
        // given
        final Long voteId = 1L;
        final RevealNameResponse revealNameResponse = RevealNameResponse.of(user, 0);

        given(voteService.revealNameHint(anyLong(), anyLong()))
            .willReturn(revealNameResponse);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/vote/{voteId}/name", voteId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .with(csrf().asHeader())
            )
            .andDo(print())
            .andDo(document("api/v1/vote/revealNameHint",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                pathParameters(parameterWithName("voteId").description("투표 아이디 값")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 투표_이름_전체_조회에_성공합니다() throws Exception {
        // given
        final Long voteId = 1L;
        final RevealFullNameResponse response = RevealFullNameResponse.of(user);

        given(voteService.revealFullName(anyLong(), anyLong()))
            .willReturn(response);

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/vote/{voteId}/fullname", voteId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer your-access-token")
                .with(csrf().asHeader())
            )
            .andDo(print())
            .andDo(document("api/v1/vote/revealFullName",
                Preprocessors.preprocessRequest(prettyPrint(), removeHeaders(excludeRequestHeaders)),
                Preprocessors.preprocessResponse(prettyPrint(), removeHeaders(excludeResponseHeaders)),
                pathParameters(parameterWithName("voteId").description("투표 아이디 값")))
            )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
