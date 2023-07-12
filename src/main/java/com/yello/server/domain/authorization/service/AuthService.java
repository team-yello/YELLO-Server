package com.yello.server.domain.authorization.service;

import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;

public interface AuthService {

    OAuthResponse oauthLogin(OAuthRequest oAuthRequest);
    Boolean isYelloIdDuplicated(String yelloId);
}
