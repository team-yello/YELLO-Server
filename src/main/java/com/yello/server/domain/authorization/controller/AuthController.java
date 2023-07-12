package com.yello.server.domain.authorization.controller;

import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;
import com.yello.server.domain.authorization.service.AuthService;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import static com.yello.server.global.common.SuccessCode.*;

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
}
