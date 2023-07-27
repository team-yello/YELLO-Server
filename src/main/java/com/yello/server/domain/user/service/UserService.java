package com.yello.server.domain.user.service;

import com.yello.server.domain.user.dto.response.UserDetailResponse;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.entity.User;

public interface UserService {

    UserDetailResponse findMyProfile(Long userId);

    UserResponse findUserById(Long userId);

    void delete(User user);

}


