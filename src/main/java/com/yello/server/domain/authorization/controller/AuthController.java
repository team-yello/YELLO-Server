package com.yello.server.domain.authorization.controller;

import static com.yello.server.global.common.SuccessCode.LOGIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.SIGN_UP_SUCCESS;
import static com.yello.server.global.common.SuccessCode.YELLOID_VALIDATION_SUCCESS;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.request.OnBoardingFriendRequest;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;
import com.yello.server.domain.authorization.dto.response.OnBoardingFriendResponse;
import com.yello.server.domain.authorization.dto.response.SignUpResponse;
import com.yello.server.domain.authorization.service.AuthService;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;

import javax.validation.constraints.NotNull;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.yello.server.global.common.SuccessCode.*;
import static com.yello.server.global.common.util.PaginationUtil.createPageable;
import static org.springframework.http.HttpHeaders.*;

@Tag(name = "03. Authentication")
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
    public BaseResponse<OAuthResponse> oauthLogin(@RequestBody OAuthRequest oAuthRequest) {
        val data = authService.oauthLogin(oAuthRequest);
        return BaseResponse.success(LOGIN_SUCCESS, data);
    }

    @Operation(summary = "옐로 아이디 중복 확인", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
    })
    @GetMapping("/valid")
    public BaseResponse<Boolean> getYelloIdValidation(@RequestParam("yelloId") String yelloId) {
        val data = authService.isYelloIdDuplicated(yelloId);
        return BaseResponse.success(YELLOID_VALIDATION_SUCCESS, data);
    }

    @PostMapping("/signup")
    public BaseResponse<SignUpResponse> postSignUp(
        @RequestHeader(AUTHORIZATION) String oAuthAccessToken,
        @Valid @RequestBody SignUpRequest signUpRequest) {
        val data = authService.signUp(oAuthAccessToken, signUpRequest);
        return BaseResponse.success(SIGN_UP_SUCCESS, data);
    }

    @PostMapping("/friend")
    public BaseResponse<OnBoardingFriendResponse> postFriendList(
            @Valid @RequestBody OnBoardingFriendRequest friendRequest,
            @NotNull @RequestParam("page") Integer page
    ) {
        val data = authService.findOnBoardingFriends(friendRequest, createPageable(page));
        return BaseResponse.success(ONBOARDING_FRIENDS_SUCCESS, data);
    }
}
