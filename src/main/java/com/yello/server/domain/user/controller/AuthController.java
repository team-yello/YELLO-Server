package com.yello.server.domain.user.controller;

import com.yello.server.domain.user.dto.response.OAuthResponse;
import com.yello.server.domain.user.dto.response.UserInfoResponse;
import com.yello.server.domain.user.service.AuthService;
import com.yello.server.domain.user.dto.request.OAuthRequest;
import com.yello.server.global.common.SuccessCode;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "소셜 로그인 API", responses = {
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = OAuthResponse.class)))),
    })
    @PostMapping("/oauth")
    public BaseResponse<OAuthResponse> oauthLogin(@RequestBody OAuthRequest authRequestDto, HttpServletRequest request) {
        return BaseResponse.success(SuccessCode.LOGIN_SUCCESS, authService.oauthLogin(authRequestDto, request));
    }

    @Operation(summary = "토큰 유저 정보 조회 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserInfoResponse.class)))),
    })
    @GetMapping("/user")
    public BaseResponse<UserInfoResponse> getUserInfo(HttpServletRequest request) {
        return BaseResponse.success(SuccessCode.USER_INFO_SUCCESS, authService.findUserInfo(request));
    }
}
