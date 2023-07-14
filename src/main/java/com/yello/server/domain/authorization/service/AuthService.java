package com.yello.server.domain.authorization.service;

import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;
import com.yello.server.domain.authorization.dto.response.SignUpResponse;

public interface AuthService {

    OAuthResponse oauthLogin(OAuthRequest oAuthRequest);
    Boolean isYelloIdDuplicated(String yelloId);
    SignUpResponse signUp(String oAuthAccessToken, SignUpRequest signUpRequest);
}
