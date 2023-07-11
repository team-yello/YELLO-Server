package com.yello.server.domain.question.controller;

import com.yello.server.domain.question.dto.response.YelloStartResponse;
import com.yello.server.domain.question.dto.response.YelloVoteResponse;
import com.yello.server.domain.question.service.YelloVoteService;
import com.yello.server.domain.vote.dto.response.VoteResponse;
import com.yello.server.global.common.SuccessCode;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "03. Question")
@RestController
@RequestMapping("api/v1/yello")
@RequiredArgsConstructor
public class YelloVoteController {

    private final YelloVoteService yelloVoteService;

    @Operation(summary = "Yello 투표 10개 조회 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = YelloVoteResponse.class)))),
    })
    @GetMapping("/vote")
    public BaseResponse<List<YelloVoteResponse>> getYelloVote(
            @RequestHeader("user-id") @Valid Long userId
    ){
        val data = yelloVoteService.findYelloVoteList(userId);
        return BaseResponse.success(SuccessCode.READ_YELLO_VOTE_SUCCESS, data);
    }

    @GetMapping
    public BaseResponse<YelloStartResponse> checkYelloStart(
            @RequestHeader("user-id") @Valid Long userId
    ){
        val data= yelloVoteService.yelloStartCheck(userId);
        return BaseResponse.success(SuccessCode.READ_YELLO_START_SUCCESS, data);
    }
}
