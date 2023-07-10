package com.yello.server.domain.user.service;

import com.yello.server.domain.user.dto.UserResponse;

public interface UserService {

    UserResponse findUser(Long userId);
}


