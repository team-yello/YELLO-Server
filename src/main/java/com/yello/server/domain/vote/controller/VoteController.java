package com.yello.server.domain.vote.controller;

import static com.yello.server.global.common.SuccessCode.CHECK_KEYWORD_SUCCESS;
import static com.yello.server.global.common.SuccessCode.CREATE_VOTE_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_VOTE_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_YELLO_START_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_YELLO_VOTE_SUCCESS;
import static com.yello.server.global.common.util.PaginationUtil.createPageable;

import com.yello.server.domain.cooldown.response.KeywordCheckResponse;
import com.yello.server.domain.cooldown.response.RevealNameResponse;
import com.yello.server.domain.cooldown.response.VoteCreateResponse;
import com.yello.server.domain.cooldown.response.VoteDetailResponse;
import com.yello.server.domain.cooldown.response.VoteListResponse;
import com.yello.server.domain.question.dto.response.VoteAvailableResponse;
import com.yello.server.domain.question.dto.response.VoteQuestionResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.vote.dto.request.CreateVoteRequest;
import com.yello.server.domain.vote.service.VoteService;
import com.yello.server.global.common.SuccessCode;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "01. Vote")
@RestController
@RequestMapping("api/v1/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @Operation(summary = "내 투표 전체 조회 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VoteListResponse.class))),
    })
    @GetMapping
    public BaseResponse<VoteListResponse> findAllMyVotes(
        @Parameter(name = "page", description = "페이지네이션 페이지 번호입니다.", example = "1")
        @RequestParam Integer page,
        @AccessTokenUser User user
    ) {
        val data = voteService.findAllVotes(user.getId(), createPageable(page));
        return BaseResponse.success(READ_VOTE_SUCCESS, data);
    }

    @Operation(summary = "투표 상세 조회 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VoteDetailResponse.class))),
    })
    @GetMapping("/{voteId}")
    public BaseResponse<VoteDetailResponse> findVote(@PathVariable Long voteId) {
        val data = voteService.findVoteById(voteId);
        return BaseResponse.success(READ_VOTE_SUCCESS, data);
    }

    @Operation(summary = "키워드 확인 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = KeywordCheckResponse.class))),
    })
    @PatchMapping("/{voteId}/keyword")
    public BaseResponse<KeywordCheckResponse> checkKeyword(
        @Parameter(name = "voteId", description = "해당 투표 아이디 값 입니다.")
        @AccessTokenUser User user,
        @PathVariable Long voteId) {
        val keywordCheckResponse = voteService.checkKeyword(user.getId(), voteId);
        return BaseResponse.success(CHECK_KEYWORD_SUCCESS, keywordCheckResponse);
    }

    @Operation(summary = "투표 10개 조회 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = VoteQuestionResponse.class)))),
    })
    @GetMapping("/question")
    public BaseResponse<List<VoteQuestionResponse>> getYelloVote(
        @AccessTokenUser User user
    ) {
        val data = voteService.findYelloVoteList(user.getId());
        return BaseResponse.success(READ_YELLO_VOTE_SUCCESS, data);
    }

    @Operation(summary = "투표 가능 여부 조회 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VoteAvailableResponse.class))),
    })
    @GetMapping("/available")
    public BaseResponse<VoteAvailableResponse> checkVoteAvailable(
        @AccessTokenUser User user
    ) {
        val data = voteService.checkVoteAvailable(user.getId());
        return BaseResponse.success(READ_YELLO_START_SUCCESS, data);
    }

    @Operation(summary = "투표 생성 API", responses = {
        @ApiResponse(
            responseCode = "201",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VoteAvailableResponse.class))),
    })
    @PostMapping
    public BaseResponse<VoteCreateResponse> createVote(
        @AccessTokenUser User user,
        @RequestBody CreateVoteRequest request
    ) {
        val data = voteService.createVote(user.getId(), request);
        return BaseResponse.success(CREATE_VOTE_SUCCESS, data);
    }

    @Operation(summary = "투표 이름 부분 조회 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RevealNameResponse.class))),
    })
    @PatchMapping("/{voteId}/name")
    public BaseResponse<RevealNameResponse> revealNameHint(
        @AccessTokenUser User user,
        @PathVariable Long voteId
    ) {
        val data = voteService.revealNameHint(user.getId(), voteId);
        return BaseResponse.success(SuccessCode.REVEAL_NAME_HINT_SUCCESS, data);
    }
}
