package com.yello.server.domain.user.service;

import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.User;
import java.util.List;

public interface UserManager {

    User getOfficialUser(Gender gender);

    List<User> getOfficialUsers();
}
