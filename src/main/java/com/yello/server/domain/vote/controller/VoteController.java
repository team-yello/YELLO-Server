package com.yello.server.domain.vote.controller;

import static com.yello.server.global.common.SuccessCode.READ_VOTE_SUCCESS;
import static com.yello.server.global.common.util.ConstantUtil.PAGE_LIMIT;

import com.yello.server.domain.vote.dto.response.VoteResponse;
import com.yello.server.domain.vote.service.VoteServiceImpl;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "01. Vote")
@RestController
@RequestMapping("api/v1/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteServiceImpl voteService;

    @Operation(summary = "내 투표 전체 조회 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = VoteResponse.class)))),
    })
    @GetMapping
    public ResponseEntity<BaseResponse> findAllMyVotes(
        @Parameter(name = "page", description = "페이지네이션 페이지 번호입니다.", example = "1")
        @RequestParam Integer page) {
        Pageable pageable = PageRequest.of(page, PAGE_LIMIT);
        val data = voteService.findAllVotes(pageable);
        return ResponseEntity.ok(BaseResponse.success(READ_VOTE_SUCCESS, data));
    }
}
