package com.yello.server.domain.auth.controller;

import com.yello.server.domain.auth.dto.response.OAuthResponse;
import com.yello.server.domain.auth.dto.response.UserInfoResponse;
import com.yello.server.domain.auth.service.AuthService;
import com.yello.server.domain.auth.dto.request.OAuthRequest;
import com.yello.server.global.common.SuccessCode;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpHeaders;
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
    public BaseResponse<OAuthResponse> oauthLogin(
            @RequestBody OAuthRequest authRequestDto,
            HttpServletRequest request) {
        val data = authService.oauthLogin(authRequestDto, request);
        return BaseResponse.success(SuccessCode.LOGIN_SUCCESS, data);
    }

    @Operation(summary = "토큰 유저 정보 조회 API", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserInfoResponse.class)))),
    })
    @GetMapping("/user")
    public BaseResponse<UserInfoResponse> getUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        val data = authService.findUserInfo(accessToken);
        return BaseResponse.success(SuccessCode.USER_INFO_SUCCESS, data);
    }
}
