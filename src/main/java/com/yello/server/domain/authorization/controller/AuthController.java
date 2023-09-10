package com.yello.server.domain.authorization.controller;

import static com.yello.server.global.common.SuccessCode.DEPARTMENT_NAME_SEARCH_BY_SCHOOL_NAME_SCHOOL_SUCCESS;
import static com.yello.server.global.common.SuccessCode.LOGIN_SUCCESS;
import static com.yello.server.global.common.SuccessCode.ONBOARDING_FRIENDS_SUCCESS;
import static com.yello.server.global.common.SuccessCode.RE_ISSUE_TOKEN_AUTH_SUCCESS;
import static com.yello.server.global.common.SuccessCode.SCHOOL_NAME_SEARCH_SCHOOL_SUCCESS;
import static com.yello.server.global.common.SuccessCode.SIGN_UP_SUCCESS;
import static com.yello.server.global.common.SuccessCode.YELLOID_VALIDATION_SUCCESS;
import static com.yello.server.global.common.factory.PaginationFactory.createPageable;

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
import com.yello.server.infrastructure.slack.annotation.SlackSignUpNotification;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/oauth")
    public BaseResponse<OAuthResponse> oauthLogin(@RequestBody OAuthRequest oAuthRequest) {
        val data = authService.oauthLogin(oAuthRequest);
        return BaseResponse.success(LOGIN_SUCCESS, data);
    }

    @GetMapping("/valid")
    public BaseResponse<Boolean> getYelloIdValidation(@RequestParam("yelloId") String yelloId) {
        val data = authService.isYelloIdDuplicated(yelloId);
        return BaseResponse.success(YELLOID_VALIDATION_SUCCESS, data);
    }

    @PostMapping("/signup")
    @SlackSignUpNotification
    public BaseResponse<SignUpResponse> postSignUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        val data = authService.signUp(signUpRequest);
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

    @GetMapping("/school")
    public BaseResponse<GroupNameSearchResponse> getSchoolList(
        @NotNull @RequestParam("keyword") String keyword,
        @NotNull @RequestParam("page") Integer page
    ) {
        val data = authService.findSchoolsByKeyword(keyword, createPageable(page));
        return BaseResponse.success(SCHOOL_NAME_SEARCH_SCHOOL_SUCCESS, data);
    }

    @GetMapping("/school/department")
    public BaseResponse<DepartmentSearchResponse> getDepartmentList(
        @NotNull @RequestParam("school") String schoolName,
        @NotNull @RequestParam("keyword") String keyword,
        @NotNull @RequestParam("page") Integer page
    ) {
        val data = authService.findDepartmentsByKeyword(schoolName, keyword, createPageable(page));
        return BaseResponse.success(DEPARTMENT_NAME_SEARCH_BY_SCHOOL_NAME_SCHOOL_SUCCESS, data);
    }

    @PostMapping("/token/issue")
    public BaseResponse<ServiceTokenVO> postReIssueToken(@ServiceToken ServiceTokenVO tokens) {
        val data = authService.reIssueToken(tokens);
        return BaseResponse.success(RE_ISSUE_TOKEN_AUTH_SUCCESS, data);
    }
}
