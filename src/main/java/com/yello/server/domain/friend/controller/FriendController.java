package com.yello.server.domain.friend.controller;

import com.yello.server.domain.friend.service.FriendServiceImpl;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.yello.server.global.common.SuccessCode.ADD_FRIEND_SUCCESS;

@Tag(name = "02. Friend")
@RestController
@RequestMapping("/api/v1/friend")
@RequiredArgsConstructor
public class FriendController {
    private final FriendServiceImpl friendService;

    @Operation(summary = "친구 추가하기 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/{friendId}")
    public ResponseEntity<BaseResponse> addFriend(
            @Parameter(name = "friendId", description = "친구 아이디 입니다.")
            @Valid @PathVariable Long friendId,
            @RequestHeader("user-id") @Valid Long userId
    ) {

        friendService.addFriend(userId, friendId);
        return ResponseEntity.ok(BaseResponse.success(ADD_FRIEND_SUCCESS));
    }
}
