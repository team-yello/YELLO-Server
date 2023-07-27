package com.yello.server.domain.user.controller;

import com.yello.server.domain.user.dto.response.UserDetailResponse;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.service.UserService;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import static com.yello.server.global.common.SuccessCode.DELETE_USER_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_USER_SUCCESS;

@Tag(name = "04. User")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailResponse.class))),
    })
    @GetMapping
    public BaseResponse<UserDetailResponse> findUser(@AccessTokenUser User user) {
        val data = userService.findMyProfile(user.getId());
        return BaseResponse.success(READ_USER_SUCCESS, data);
    }

    @Operation(summary = "유저 정보 조회 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
    })
    @GetMapping("/{userId}")
    public BaseResponse<UserResponse> findUserById(@PathVariable Long userId) {
        val data = userService.findUserById(userId);
        return BaseResponse.success(READ_USER_SUCCESS, data);
    }

    @Operation(summary = "유저 탈퇴 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping
    public BaseResponse deleteUser(@AccessTokenUser User user) {
        userService.delete(user);
        return BaseResponse.success(DELETE_USER_SUCCESS);
    }
}
