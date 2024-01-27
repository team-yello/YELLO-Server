package com.yello.server.domain.user.repository;

import com.yello.server.domain.user.entity.UserData;

public interface UserDataRepository {

    UserData save(UserData userData);

}
