package com.yello.server.domain.group.repository;

import static com.yello.server.global.common.ErrorCode.GROUPID_NOT_FOUND_GROUP_EXCEPTION;

import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.group.exception.GroupNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserGroupRepositoryImpl implements UserGroupRepository {

    private final UserGroupJpaRepository userGroupJpaRepository;


    @Override
    public UserGroup save(UserGroup userGroup) {
        return userGroupJpaRepository.save(userGroup);
    }

    @Override
    public UserGroup getById(Long id) {
        return userGroupJpaRepository.findById(id)
            .orElseThrow(() -> new GroupNotFoundException(GROUPID_NOT_FOUND_GROUP_EXCEPTION));
    }

    @Override
    public Optional<UserGroup> findById(Long id) {
        return userGroupJpaRepository.findById(id);
    }

    @Override
    public List<UserGroup> findAllByGroupName(String groupName) {
        return userGroupJpaRepository.findAllByGroupName(groupName);
    }

    @Override
    public Integer countDistinctGroupNameContaining(String groupName, UserGroupType userGroupType) {
        return userGroupJpaRepository.countDistinctGroupNameContaining(groupName, userGroupType);
    }

    @Override
    public List<String> findDistinctGroupNameContaining(String groupName, UserGroupType userGroupType,
        Pageable pageable) {
        return userGroupJpaRepository.findDistinctGroupNameContaining(groupName, userGroupType, pageable);
    }

    @Override
    public Integer countAllContaining(String groupName, UserGroupType userGroupType) {
        return userGroupJpaRepository.countAllContaining(groupName, userGroupType);
    }

    @Override
    public List<UserGroup> findAllContaining(String groupName, UserGroupType userGroupType, Pageable pageable) {
        return userGroupJpaRepository.findAllContaining(groupName, userGroupType, pageable);
    }

    @Override
    public Integer countAllByGroupNameContaining(String groupName, String subGroupName, UserGroupType userGroupType) {
        return userGroupJpaRepository.countAllByGroupNameContaining(groupName, subGroupName, userGroupType);
    }

    @Override
    public List<UserGroup> findAllByGroupNameContaining(String groupName, String subGroupName,
        UserGroupType userGroupType, Pageable pageable) {
        return userGroupJpaRepository.findAllByGroupNameContaining(groupName, subGroupName, userGroupType, pageable);
    }

    @Override
    public Optional<UserGroup> findByGroupNameAndSubGroupName(String groupName, String subGroupName,
        UserGroupType userGroupType) {
        return userGroupJpaRepository.findByGroupNameAndSubGroupName(groupName, subGroupName, userGroupType);
    }

    @Override
    public UserGroup getByGroupNameAndSubGroupName(String groupName, String subGroupName, UserGroupType userGroupType) {
        return userGroupJpaRepository.findByGroupNameAndSubGroupName(groupName, subGroupName, userGroupType)
            .orElseThrow(() -> new GroupNotFoundException(GROUPID_NOT_FOUND_GROUP_EXCEPTION));
    }
}
