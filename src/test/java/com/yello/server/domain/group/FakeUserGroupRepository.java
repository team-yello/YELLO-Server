package com.yello.server.domain.group;

import static com.yello.server.global.common.ErrorCode.GROUPID_NOT_FOUND_GROUP_EXCEPTION;

import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.group.exception.GroupNotFoundException;
import com.yello.server.domain.group.repository.UserGroupRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public class FakeUserGroupRepository implements UserGroupRepository {

    private final List<UserGroup> data = new ArrayList<>();
    private Long id = 0L;

    @Override
    public UserGroup save(UserGroup group) {
        if (group.getId() != null && group.getId() > id) {
            id = group.getId();
        }

        UserGroup newUserGroup = UserGroup.builder()
            .id(group.getId() == null ? ++id : group.getId())
            .groupName(group.getGroupName())
            .subGroupName(group.getSubGroupName())
            .userGroupType(group.getUserGroupType())
            .build();

        data.add(newUserGroup);
        return newUserGroup;
    }

    @Override
    public UserGroup getById(Long id) {
        return data.stream()
            .filter(group -> group.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new GroupNotFoundException(GROUPID_NOT_FOUND_GROUP_EXCEPTION));
    }

    @Override
    public Optional<UserGroup> findById(Long id) {
        return data.stream()
            .filter(group -> group.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<UserGroup> findAllByGroupName(String groupName) {
        return data.stream()
            .filter(group -> group.getGroupName().equals(groupName))
            .toList();
    }

    @Override
    public Integer countDistinctGroupNameContaining(String groupName, UserGroupType userGroupType) {
        return Math.toIntExact(data.stream()
            .filter(group -> group.getGroupName().contains(groupName)
                && group.getUserGroupType() == userGroupType)
            .map(UserGroup::getGroupName)
            .count());
    }

    @Override
    public List<String> findDistinctGroupNameContaining(String groupName, UserGroupType userGroupType,
        Pageable pageable) {
        return data.stream()
            .filter(group -> group.getGroupName().contains(groupName)
                && group.getUserGroupType() == userGroupType)
            .map(UserGroup::getGroupName)
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .toList();
    }

    @Override
    public Integer countAllContaining(String schoolName, UserGroupType userGroupType) {
        return Math.toIntExact(data.stream()
            .filter(group -> group.getGroupName().contains(schoolName)
                && group.getUserGroupType() == userGroupType)
            .count());
    }

    @Override
    public List<UserGroup> findAllContaining(String schoolName, UserGroupType userGroupType, Pageable pageable) {
        return data.stream()
            .filter(group -> group.getGroupName().equals(schoolName))
            .filter(group -> group.getUserGroupType() == userGroupType)
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .toList();
    }

    @Override
    public Integer countAllByGroupNameContaining(String schoolName, String departmentName,
        UserGroupType userGroupType) {
        return Math.toIntExact(data.stream()
            .filter(group -> group.getGroupName().equals(schoolName))
            .filter(group -> group.getSubGroupName().contains(departmentName))
            .filter(group -> group.getUserGroupType() == userGroupType)
            .count());
    }

    @Override
    public List<UserGroup> findAllByGroupNameContaining(String schoolName, String departmentName,
        UserGroupType userGroupType,
        Pageable pageable) {
        return data.stream()
            .filter(group -> group.getGroupName().equals(schoolName))
            .filter(group -> group.getSubGroupName().contains(departmentName))
            .filter(group -> group.getUserGroupType() == userGroupType)
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .toList();
    }

    @Override
    public Optional<UserGroup> findByGroupNameAndSubGroupName(String schoolName, String departmentName,
        UserGroupType userGroupType) {
        return data.stream()
            .filter(group -> group.getGroupName().equals(schoolName))
            .filter(group -> group.getSubGroupName().equals(departmentName))
            .filter(group -> group.getUserGroupType() == userGroupType)
            .findFirst();
    }

    @Override
    public UserGroup getByGroupNameAndSubGroupName(String schoolName, String departmentName,
        UserGroupType userGroupType) {
        return data.stream()
            .filter(group -> group.getGroupName().equals(schoolName))
            .filter(group -> group.getSubGroupName().equals(departmentName))
            .filter(group -> group.getUserGroupType() == userGroupType)
            .findFirst()
            .orElseThrow(() -> new GroupNotFoundException(GROUPID_NOT_FOUND_GROUP_EXCEPTION));
    }
}
