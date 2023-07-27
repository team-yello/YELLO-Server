package com.yello.server.domain.friend.controller;

import com.yello.server.domain.friend.dto.request.KakaoRecommendRequest;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.dto.response.FriendsResponse;
import com.yello.server.domain.friend.dto.response.RecommendFriendResponse;
import com.yello.server.domain.friend.service.FriendService;
import com.yello.server.domain.user.entity.User;
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
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.yello.server.global.common.SuccessCode.*;
import static com.yello.server.global.common.factory.PaginationFactory.createPageable;
import static com.yello.server.global.common.factory.PaginationFactory.createPageableByNameSort;


@Tag(name = "02. Friend")
@RestController
@RequestMapping("/api/v1/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 추가하기 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/{targetId}")
    public BaseResponse addFriend(
            @Parameter(name = "targetId", description = "친구 신청할 상대 유저의 아이디 값 입니다.")
            @Valid @PathVariable Long targetId,
            @AccessTokenUser User user) {
        friendService.addFriend(user.getId(), targetId);
        return BaseResponse.success(ADD_FRIEND_SUCCESS);
    }

    @Operation(summary = "내 친구 전체 조회 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FriendsResponse.class))
            )
    })
    @GetMapping
    public BaseResponse<FriendsResponse> findAllFriend(
            @Parameter(name = "page", description = "페이지네이션 페이지 번호입니다.", example = "1")
            @Valid @RequestParam Integer page,
            @AccessTokenUser User user) {
        val data = friendService.findAllFriends(createPageable(page), user.getId());
        return BaseResponse.success(READ_FRIEND_SUCCESS, data);
    }

    @Operation(summary = "친구 셔플 조회 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FriendShuffleResponse.class))))
    })
    @GetMapping("/shuffle")
    public BaseResponse<List<FriendShuffleResponse>> findShuffledFriend(
            @AccessTokenUser User user) {
        val friendShuffleResponse = friendService.findShuffledFriend(user.getId());
        return BaseResponse.success(SuccessCode.SHUFFLE_FRIEND_SUCCESS, friendShuffleResponse);
    }

    @Operation(summary = "그룹 추천친구 조회 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecommendFriendResponse.class)))
    })
    @GetMapping("/recommend/school")
    public BaseResponse<RecommendFriendResponse> recommendSchoolFriend(
            @Parameter(name = "page", description = "페이지네이션 페이지 번호입니다.", example = "1")
            @Valid @RequestParam Integer page,
            @AccessTokenUser User user
    ) {
        val data = friendService.findAllRecommendSchoolFriends(createPageableByNameSort(page), user.getId());
        return BaseResponse.success(SuccessCode.READ_FRIEND_SUCCESS, data);
    }

    @Operation(summary = "카카오 추천 친구 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecommendFriendResponse.class)))
    })
    @PostMapping("/recommend/kakao")
    public BaseResponse<RecommendFriendResponse> recommendKakaoFriend(
            @RequestBody KakaoRecommendRequest request,
            @Valid @RequestParam Integer page,
            @AccessTokenUser User user
    ) {
        val data = friendService.findAllRecommendKakaoFriends(createPageableByNameSort(page), user.getId(), request);
        return BaseResponse.success(READ_FRIEND_SUCCESS, data);
    }

    @Operation(summary = "친구 삭제하기 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{targetId}")
    public BaseResponse deleteFriend(
            @Parameter(name = "targetId", description = "삭제할 친구 유저의 아이디 값 입니다.")
            @Valid @PathVariable Long targetId,
            @AccessTokenUser User user) {
        friendService.deleteFriend(user.getId(), targetId);
        return BaseResponse.success(DELETE_FRIEND_SUCCESS);
    }
}
