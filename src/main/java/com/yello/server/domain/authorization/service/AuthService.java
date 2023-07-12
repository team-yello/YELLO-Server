package com.yello.server.domain.authorization.service;

import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.request.SignInRequest;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;
import com.yello.server.domain.authorization.dto.response.SignInResponse;

public interface AuthService {

    OAuthResponse oauthLogin(OAuthRequest oAuthRequest);
    Boolean isYelloIdDuplicated(String yelloId);
    SignInResponse signIn(String oAuthAccessToken, SignInRequest signInRequest);
}
