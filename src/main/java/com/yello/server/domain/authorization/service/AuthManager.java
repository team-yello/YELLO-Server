package com.yello.server.domain.authorization.service;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.user.entity.User;

public interface AuthManager {


    User getSignedInUserByUuid(String uuid);

    ServiceTokenVO issueToken(User user);

    boolean isExpired(String token);

    String issueNewAccessToken(String refreshToken);

    void validateSignupRequest(SignUpRequest signUpRequest);

    Boolean renewUserData(User user);
}
