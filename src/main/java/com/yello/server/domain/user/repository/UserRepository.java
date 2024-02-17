package com.yello.server.domain.user.repository;

import com.yello.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByIdNotFiltered(Long id);

    User getById(Long id);

    User getByIdNotFiltered(Long id);

    Optional<User> findByUuid(String uuid);

    Optional<User> findByUuidNotFiltered(String uuid);

    User getByUuid(String uuid);

    boolean existsByUuid(String uuid);

    User getByYelloId(String yelloId);

    User getByYelloIdNotFiltered(String yelloId);

    Optional<User> findByYelloId(String yelloId);

    Optional<User> findByYelloIdNotFiltered(String yelloId);

    User getByDeviceToken(String deviceToken);

    User getByDeviceTokenNotFiltered(String deviceToken);

    Optional<User> findByDeviceToken(String deviceToken);

    Optional<User> findByDeviceTokenNotFiltered(String deviceToken);

    List<User> findAllByGroupId(Long groupId);

    Integer countAllByGroupNameFilteredByNotFriend(Long userId, String groupName);

    List<User> findAllByGroupNameFilteredByNotFriend(Long userId, String groupName, Pageable pageable);

    List<User> findAllByGroupContainingName(String groupName, String keyword,
        List<String> uuidList);

    List<User> findAllByOtherGroupContainingName(String groupName, String keyword,
        List<String> uuidList);

    List<User> findAllByGroupContainingYelloId(String groupName, String keyword,
        List<String> uuidList);

    List<User> findAllByOtherGroupContainingYelloId(String groupName, String keyword,
        List<String> uuidList);

    List<User> findAllByGroupNameContainingAndFriendListNotContaining(String keyword, List<String> uuidList, List<User> friendList);

    Long count();

    Long countAllByYelloIdContaining(String yelloId);

    Long countAllByNameContaining(String name);

    Page<User> findAll(Pageable pageable);

    Page<User> findAllByYelloIdContaining(Pageable pageable, String yelloId);

    Page<User> findAllByNameContaining(Pageable pageable, String name);

    void delete(User user);
}
