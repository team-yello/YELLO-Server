package com.yello.server.domain.authorization.controller;

import static com.yello.server.global.common.SuccessCode.DEPARTMENT_NAME_SEARCH_BY_SCHOOL_NAME_SCHOOL_SUCCESS;
import static com.yello.server.global.common.SuccessCode.LOGIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.ONBOARDING_FRIENDS_SUCCESS;
import static com.yello.server.global.common.SuccessCode.RE_ISSUE_TOKEN_AUTH_SUCCESS;
import static com.yello.server.global.common.SuccessCode.SCHOOL_NAME_SEARCH_SCHOOL_SUCCESS;
import static com.yello.server.global.common.SuccessCode.SIGN_UP_SUCCESS;
import static com.yello.server.global.common.SuccessCode.YELLOID_VALIDATION_SUCCESS;
import static com.yello.server.global.common.util.PaginationUtil.createPageable;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.request.OnBoardingFriendRequest;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.authorization.dto.response.DepartmentSearchResponse;
import com.yello.server.domain.authorization.dto.response.GroupNameSearchResponse;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;
import com.yello.server.domain.authorization.dto.response.OnBoardingFriendResponse;
import com.yello.server.domain.authorization.dto.response.SignUpResponse;
import com.yello.server.domain.authorization.service.AuthService;
import com.yello.server.global.common.annotation.ServiceToken;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @Operation(summary = "옐로 아이디 중복 확인 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
    })
    @GetMapping("/valid")
    public BaseResponse<Boolean> getYelloIdValidation(@RequestParam("yelloId") String yelloId) {
        val data = authService.isYelloIdDuplicated(yelloId);
        return BaseResponse.success(YELLOID_VALIDATION_SUCCESS, data);
    }

    @Operation(summary = "회원가입 API", responses = {
        @ApiResponse(
            responseCode = "201",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SignUpResponse.class))),
    })
    @PostMapping("/signup")
    public BaseResponse<SignUpResponse> postSignUp(
        @Valid @RequestBody SignUpRequest signUpRequest) {
        val data = authService.signUp(signUpRequest);
        return BaseResponse.success(SIGN_UP_SUCCESS, data);
    }

    @Operation(summary = "가입한 친구 목록 불러오기 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OnBoardingFriendResponse.class))),
    })
    @PostMapping("/friend")
    public BaseResponse<OnBoardingFriendResponse> postFriendList(
        @Valid @RequestBody OnBoardingFriendRequest friendRequest,
        @NotNull @RequestParam("page") Integer page
    ) {
        val data = authService.findOnBoardingFriends(friendRequest, createPageable(page));
        return BaseResponse.success(ONBOARDING_FRIENDS_SUCCESS, data);
    }

    @Operation(summary = "대학교 이름 검색하기 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GroupNameSearchResponse.class))),
    })
    @GetMapping("/school/school")
    public BaseResponse<GroupNameSearchResponse> getSchoolList(
        @NotNull @RequestParam("search") String keyword,
        @NotNull @RequestParam("page") Integer page
    ) {
        val data = authService.findSchoolsBySearch(keyword, createPageable(page));
        return BaseResponse.success(SCHOOL_NAME_SEARCH_SCHOOL_SUCCESS, data);
    }

    @Operation(summary = "대학교 이름으로 학과 이름 검색하기 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepartmentSearchResponse.class))),
    })
    @GetMapping("/school/department")
    public BaseResponse<DepartmentSearchResponse> getDepartmentList(
        @NotNull @RequestParam("school") String schoolName,
        @NotNull @RequestParam("search") String keyword,
        @NotNull @RequestParam("page") Integer page
    ) {
        val data = authService.findDepartmentsBySearch(schoolName, keyword, createPageable(page));
        return BaseResponse.success(DEPARTMENT_NAME_SEARCH_BY_SCHOOL_NAME_SCHOOL_SUCCESS, data);
    }

    @Operation(summary = "토큰 재발급 API", responses = {
        @ApiResponse(
            responseCode = "201",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceTokenVO.class))),
    })
    @PostMapping("/token/issue")
    public BaseResponse<ServiceTokenVO> postReIssueToken( @ServiceToken ServiceTokenVO tokens ) {
        val data = authService.reIssueToken(tokens);
        return BaseResponse.success(RE_ISSUE_TOKEN_AUTH_SUCCESS, data);
    }
}
