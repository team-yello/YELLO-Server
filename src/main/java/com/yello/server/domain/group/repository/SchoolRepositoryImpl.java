package com.yello.server.domain.group.repository;

import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.group.exception.GroupNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.yello.server.global.common.ErrorCode.GROUPID_NOT_FOUND_GROUP_EXCEPTION;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolRepositoryImpl implements SchoolRepository {

    private final SchoolJpaRepository schoolJpaRepository;


    @Override
    public School findById(Long id) {
        return schoolJpaRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(GROUPID_NOT_FOUND_GROUP_EXCEPTION));
    }

    @Override
    public Integer countDistinctSchoolNameContaining(String schoolName) {
        return schoolJpaRepository.countDistinctSchoolNameContaining(schoolName);
    }

    @Override
    public List<String> findDistinctSchoolNameContaining(String schoolName, Pageable pageable) {
        return schoolJpaRepository.findDistinctSchoolNameContaining(schoolName, pageable);
    }

    @Override
    public Integer countAllBySchoolNameContaining(String schoolName, String departmentName) {
        return schoolJpaRepository.countAllBySchoolNameContaining(schoolName, departmentName);
    }

    @Override
    public List<School> findAllBySchoolNameContaining(String schoolName, String departmentName, Pageable pageable) {
        return schoolJpaRepository.findAllBySchoolNameContaining(schoolName, departmentName, pageable);
    }
}
