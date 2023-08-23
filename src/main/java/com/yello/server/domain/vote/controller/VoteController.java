package com.yello.server.domain.vote.controller;

import static com.yello.server.global.common.SuccessCode.CHECK_KEYWORD_SUCCESS;
import static com.yello.server.global.common.SuccessCode.CREATE_VOTE_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_VOTE_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_YELLO_START_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_YELLO_VOTE_SUCCESS;
import static com.yello.server.global.common.factory.PaginationFactory.createPageableLimitTen;

import com.yello.server.domain.keyword.dto.response.KeywordCheckResponse;
import com.yello.server.domain.question.dto.response.QuestionForVoteResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.vote.dto.request.CreateVoteRequest;
import com.yello.server.domain.vote.dto.response.RevealFullNameResponse;
import com.yello.server.domain.vote.dto.response.RevealNameResponse;
import com.yello.server.domain.vote.dto.response.VoteAvailableResponse;
import com.yello.server.domain.vote.dto.response.VoteCreateResponse;
import com.yello.server.domain.vote.dto.response.VoteDetailResponse;
import com.yello.server.domain.vote.dto.response.VoteFriendResponse;
import com.yello.server.domain.vote.dto.response.VoteListResponse;
import com.yello.server.domain.vote.dto.response.VoteUnreadCountResponse;
import com.yello.server.domain.vote.service.VoteService;
import com.yello.server.global.common.SuccessCode;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import com.yello.server.infrastructure.firebase.service.NotificationService;
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

@RestController
@RequestMapping("api/v1/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final NotificationService notificationService;

    @GetMapping
    public BaseResponse<VoteListResponse> findAllMyVotes(@RequestParam Integer page, @AccessTokenUser User user) {
        val data = voteService.findAllVotes(user.getId(), createPageableLimitTen(page));
        return BaseResponse.success(READ_VOTE_SUCCESS, data);
    }

    @GetMapping("/count")
    public BaseResponse<VoteUnreadCountResponse> getUnreadVoteCount(@AccessTokenUser User user) {
        val data = voteService.getUnreadVoteCount(user.getId());
        return BaseResponse.success(READ_VOTE_SUCCESS, data);
    }

    @GetMapping("/friend")
    public BaseResponse<VoteFriendResponse> findAllFriendVotes(@RequestParam Integer page, @AccessTokenUser User user) {
        val data = voteService.findAllFriendVotes(user.getId(), createPageableLimitTen(page));
        return BaseResponse.success(READ_VOTE_SUCCESS, data);
    }

    @GetMapping("/{voteId}")
    public BaseResponse<VoteDetailResponse> findVote(@PathVariable Long voteId, @AccessTokenUser User user) {
        val data = voteService.findVoteById(voteId, user.getId());
        return BaseResponse.success(READ_VOTE_SUCCESS, data);
    }

    @PatchMapping("/{voteId}/keyword")
    public BaseResponse<KeywordCheckResponse> checkKeyword(@PathVariable Long voteId, @AccessTokenUser User user) {
        val keywordCheckResponse = voteService.checkKeyword(user.getId(), voteId);
        return BaseResponse.success(CHECK_KEYWORD_SUCCESS, keywordCheckResponse);
    }

    @GetMapping("/question")
    public BaseResponse<List<QuestionForVoteResponse>> findVoteQuestions(@AccessTokenUser User user) {
        val data = voteService.findVoteQuestionList(user.getId());
        return BaseResponse.success(READ_YELLO_VOTE_SUCCESS, data);
    }

    @GetMapping("/available")
    public BaseResponse<VoteAvailableResponse> checkVoteAvailable(@AccessTokenUser User user) {
        val data = voteService.checkVoteAvailable(user.getId());
        return BaseResponse.success(READ_YELLO_START_SUCCESS, data);
    }

    @PostMapping
    public BaseResponse<VoteCreateResponse> createVote(
        @AccessTokenUser User user,
        @RequestBody CreateVoteRequest request
    ) {
        val data = voteService.createVote(user.getId(), request);
        data.votes().forEach(notificationService::sendYelloNotification);

        val response = VoteCreateResponse.of(data.point());
        return BaseResponse.success(CREATE_VOTE_SUCCESS, response);
    }

    @PatchMapping("/{voteId}/name")
    public BaseResponse<RevealNameResponse> revealNameHint(@AccessTokenUser User user, @PathVariable Long voteId) {
        val data = voteService.revealNameHint(user.getId(), voteId);
        return BaseResponse.success(SuccessCode.REVEAL_NAME_HINT_SUCCESS, data);
    }

    @PatchMapping("/{voteId}/fullname")
    public BaseResponse<RevealFullNameResponse> revealFullName(@AccessTokenUser User user, @PathVariable Long voteId) {
        val data = voteService.revealFullName(user.getId(), voteId);
        return BaseResponse.success(SuccessCode.REVEAL_NAME_SUCCESS, data);
    }

}
