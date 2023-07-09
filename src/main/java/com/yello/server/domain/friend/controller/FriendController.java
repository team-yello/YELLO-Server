package com.yello.server.domain.friend.controller;

import com.yello.server.domain.friend.dto.FriendsResponse;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.service.FriendService;
import com.yello.server.global.common.SuccessCode;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.yello.server.global.common.SuccessCode.ADD_FRIEND_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_FRIEND_SUCCESS;
import static com.yello.server.global.common.util.PaginationUtil.createPageable;

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
            @RequestHeader("user-id") @Valid Long userId) {
        friendService.addFriend(userId, targetId);
        return BaseResponse.success(ADD_FRIEND_SUCCESS);
    }

    @Operation(summary = "내 친구 전체 조회 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FriendsResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<BaseResponse> findAllFriend(
            @Parameter(name = "page", description = "페이지네이션 페이지 번호입니다.", example = "1")
            @Valid @RequestParam Integer page,
            @RequestHeader("user-id") @Valid Long userId) {
        friendService.findAllFriends(createPageable(page), userId);
        return ResponseEntity.ok(BaseResponse.success(READ_FRIEND_SUCCESS));
    }

    @Operation(summary = "친구 셔플 조회 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FriendShuffleResponse.class))
            )
    })
    @GetMapping("/shuffle")
    public BaseResponse<List<FriendShuffleResponse>> shuffleFriend(
            @RequestHeader("user-id") @Valid Long userId) {
        List<FriendShuffleResponse> friendShuffleResponse = friendService.shuffleFriend(userId);

        return BaseResponse.success(SuccessCode.SHUFFLE_FRIEND_SUCCESS, friendShuffleResponse);

    }

}
