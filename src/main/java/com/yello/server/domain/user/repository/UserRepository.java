package com.yello.server.domain.user.repository;

import com.yello.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    User getById(Long id);

    Optional<User> findByUuid(String uuid);

    User getByUuid(String uuid);

    boolean existsByUuid(String uuid);

    Optional<User> findByYelloId(String yelloId);

    User getByYelloId(String yelloId);

    List<User> findAllByGroupId(Long groupId);

    List<User> findAllByGroupContainingName(String groupName, String keyword);

    List<User> findAllByOtherGroupContainingName(String groupName, String keyword);

    List<User> findAllByGroupContainingYelloId(String groupName, String keyword);

    List<User> findAllByOtherGroupContainingYelloId(String groupName, String keyword);

}
