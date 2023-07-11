package com.yello.server.domain.authorization.service;

import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;
import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    OAuthResponse oauthLogin(OAuthRequest authRequestDto, HttpServletRequest request);
}
