package com.yello.server.domain.authorization.controller;

import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;
import com.yello.server.domain.authorization.service.AuthService;
import com.yello.server.global.common.SuccessCode;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "소셜 로그인 API", responses = {
        @ApiResponse(
            responseCode = "201",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OAuthResponse.class))),
    })
    @PostMapping("/oauth")
    public BaseResponse<OAuthResponse> oauthLogin(
        @RequestBody OAuthRequest authRequestDto,
        HttpServletRequest request) {
        val data = authService.oauthLogin(authRequestDto, request);
        return BaseResponse.success(SuccessCode.LOGIN_SUCCESS, data);
    }
}
