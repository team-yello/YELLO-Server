package com.yello.server.domain.auth.service;

import com.yello.server.domain.auth.dto.request.OAuthRequest;
import com.yello.server.domain.auth.dto.response.OAuthResponse;
import com.yello.server.domain.auth.dto.response.UserInfoResponse;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    OAuthResponse oauthLogin(OAuthRequest authRequestDto, HttpServletRequest request);
    UserInfoResponse findUserInfo(String accessToken);
}
