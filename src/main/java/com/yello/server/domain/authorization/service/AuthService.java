package com.yello.server.domain.authorization.service;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.request.OnBoardingFriendRequest;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.authorization.dto.response.*;
import com.yello.server.domain.user.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthService {
    OAuthResponse oauthLogin(OAuthRequest oAuthRequest);
    Boolean isYelloIdDuplicated(String yelloId);
    SignUpResponse signUp(SignUpRequest signUpRequest);
    GroupNameSearchResponse findSchoolsBySearch(String keyword, Pageable pageable);
    DepartmentSearchResponse findDepartmentsBySearch(String schoolName, String keyword, Pageable pageable);
    OnBoardingFriendResponse findOnBoardingFriends(OnBoardingFriendRequest friendRequest, Pageable pageable);
    ServiceTokenVO reIssueToken(ServiceTokenVO token);
}
