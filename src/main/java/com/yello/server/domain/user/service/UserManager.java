package com.yello.server.domain.user.service;

import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.User;

public interface UserManager {

    User getOfficialUser(Gender gender);

}
