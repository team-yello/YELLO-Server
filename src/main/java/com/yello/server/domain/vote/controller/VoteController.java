package com.yello.server.domain.vote.controller;

import com.yello.server.domain.question.dto.response.VoteAvailableResponse;
import com.yello.server.domain.question.dto.response.VoteQuestionResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.vote.dto.request.CreateVoteRequest;
import com.yello.server.domain.vote.dto.response.KeywordCheckResponse;
import com.yello.server.domain.vote.dto.response.VoteDetailResponse;
import com.yello.server.domain.vote.dto.response.VoteResponse;
import com.yello.server.domain.vote.service.VoteService;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.yello.server.global.common.SuccessCode.*;
import static com.yello.server.global.common.util.PaginationUtil.createPageable;

@Tag(name = "01. Vote")
@RestController
@RequestMapping("api/v1/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @Operation(summary = "내 투표 전체 조회 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = VoteResponse.class)))),
    })
    @GetMapping
    public BaseResponse<List<VoteResponse>> findAllMyVotes(
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

    @Operation(summary = "Yello 투표 10개 조회 API", responses = {
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

    @GetMapping("/available")
    public BaseResponse<VoteAvailableResponse> checkVoteAvailable(
            @AccessTokenUser User user
    ) {
        val data = voteService.checkVoteAvailable(user.getId());
        return BaseResponse.success(READ_YELLO_START_SUCCESS, data);
    }

    @Operation(summary = "Yello 투표 생성 API", responses = {
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(mediaType = "application/json")),
    })
    @PostMapping
    public BaseResponse createVote(
            @AccessTokenUser User user,
            @RequestBody CreateVoteRequest request
    ) {
        voteService.createVote(user.getId(), request);
        return BaseResponse.success(CREATE_VOTE_SUCCESS);
    }
}
