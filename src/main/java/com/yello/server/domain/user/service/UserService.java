package com.yello.server.domain.user.service;

import com.yello.server.domain.user.dto.UserResponse;
import com.yello.server.domain.user.entity.User;

public interface UserService {

    UserResponse findUser(Long userId);

    User findByUserId(Long userId);

    void delete(User user);

}


