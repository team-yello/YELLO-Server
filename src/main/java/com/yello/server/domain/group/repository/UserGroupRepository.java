package com.yello.server.domain.group.repository;

import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.entity.UserGroupType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface UserGroupRepository {

    UserGroup save(UserGroup userGroup);

    UserGroup getById(Long id);

    Optional<UserGroup> findById(Long id);

    List<UserGroup> findAllByGroupName(String groupName);

    Integer countDistinctGroupNameContaining(String groupName, UserGroupType userGroupType);

    List<String> findDistinctGroupNameContaining(String groupName, UserGroupType userGroupType, Pageable pageable);

    Integer countAllContaining(String groupName, UserGroupType userGroupType);

    List<UserGroup> findAllContaining(String groupName, UserGroupType userGroupType, Pageable pageable);

    Integer countAllByGroupNameContaining(String groupName, String subGroupName, UserGroupType userGroupType);

    List<UserGroup> findAllByGroupNameContaining(String groupName, String subGroupName, UserGroupType userGroupType,
        Pageable pageable);

    Optional<UserGroup> findByGroupNameAndSubGroupName(String groupName, String subGroupName,
        UserGroupType userGroupType);

    UserGroup getByGroupNameAndSubGroupName(String groupName, String subGroupName, UserGroupType userGroupType);
}
