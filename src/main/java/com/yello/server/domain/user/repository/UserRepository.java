package com.yello.server.domain.user.repository;

import com.yello.server.domain.user.entity.User;
import java.util.List;

public interface UserRepository {

    User save(User user);

    User findById(Long id);

    User findByUuid(String uuid);

    boolean existsByUuid(String uuid);

    User findByYelloId(String yelloId);

    List<User> findAllByGroupId(Long groupId);

}
