package com.yello.server.domain.user.repository;

import com.yello.server.domain.user.entity.UserData;
import com.yello.server.domain.user.entity.UserDataType;
import java.util.Optional;

public interface UserDataRepository {

    UserData save(UserData userData);

    void update(Long userId, UserDataType tag, String value);

    UserData getByUserIdAndTag(Long userId, UserDataType tag);

    Optional<UserData> findByUserIdAndTag(Long userId, UserDataType tag);
}
