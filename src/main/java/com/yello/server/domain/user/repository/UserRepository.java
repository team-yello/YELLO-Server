package com.yello.server.domain.user.repository;

import com.yello.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    User getById(Long id);

    Optional<User> findByUuid(String uuid);

    Optional<User> findByUuidNotFiltered(String uuid);

    User getByUuid(String uuid);

    boolean existsByUuid(String uuid);

    Optional<User> findByYelloId(String yelloId);

    Optional<User> findByYelloIdNotFiltered(String yelloId);

    User getByYelloId(String yelloId);

    Optional<User> findByDeviceToken(String deviceToken);

    Optional<User> findByDeviceTokenNotFiltered(String deviceToken);

    List<User> findAllByGroupId(String schoolName);

    List<User> findAllByGroupContainingName(String groupName, String keyword,
        List<String> uuidList);

    List<User> findAllByOtherGroupContainingName(String groupName, String keyword,
        List<String> uuidList);

    List<User> findAllByGroupContainingYelloId(String groupName, String keyword,
        List<String> uuidList);

    List<User> findAllByOtherGroupContainingYelloId(String groupName, String keyword,
        List<String> uuidList);

    Long count();

    Long countAllByYelloIdContaining(String yelloId);

    Page<User> findAll(Pageable pageable);

    Page<User> findAllContaining(Pageable pageable, String yelloId);

    void delete(User user);
}
