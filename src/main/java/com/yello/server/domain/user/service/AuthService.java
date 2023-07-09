package com.yello.server.domain.user.service;

import com.yello.server.domain.user.dto.request.OAuthRequest;
import com.yello.server.domain.user.dto.response.OAuthResponse;
import com.yello.server.domain.user.dto.response.UserInfoResponse;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    OAuthResponse oauthLogin(OAuthRequest authRequestDto, HttpServletRequest request);
    UserInfoResponse findUserInfo(HttpServletRequest request);
}
