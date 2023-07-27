package com.yello.server.domain.authorization.service;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.request.OnBoardingFriendRequest;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.authorization.dto.response.DepartmentSearchResponse;
import com.yello.server.domain.authorization.dto.response.GroupNameSearchResponse;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;
import com.yello.server.domain.authorization.dto.response.OnBoardingFriendResponse;
import com.yello.server.domain.authorization.dto.response.SignUpResponse;
import org.springframework.data.domain.Pageable;

public interface AuthService {

    OAuthResponse oauthLogin(OAuthRequest oAuthRequest);

    Boolean isYelloIdDuplicated(String yelloId);

    SignUpResponse signUp(SignUpRequest signUpRequest);

    GroupNameSearchResponse findSchoolsBySearch(String keyword, Pageable pageable);

    DepartmentSearchResponse findDepartmentsBySearch(String schoolName, String keyword, Pageable pageable);

    OnBoardingFriendResponse findOnBoardingFriends(OnBoardingFriendRequest friendRequest, Pageable pageable);

    ServiceTokenVO reIssueToken(ServiceTokenVO token);
}
