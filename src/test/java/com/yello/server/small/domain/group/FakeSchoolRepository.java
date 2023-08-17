package com.yello.server.small.domain.group;

import static com.yello.server.global.common.ErrorCode.GROUPID_NOT_FOUND_GROUP_EXCEPTION;

import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.group.exception.GroupNotFoundException;
import com.yello.server.domain.group.repository.SchoolRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public class FakeSchoolRepository implements SchoolRepository {

    private final List<School> data = new ArrayList<>();
    private Long id = 0L;

    @Override
    public School save(School school) {
        if (school.getId()!=null && school.getId() > id) {
            id = school.getId();
        }

        School newSchool = School.builder()
            .id(school.getId()==null ? ++id : school.getId())
            .schoolName(school.getSchoolName())
            .departmentName(school.getDepartmentName())
            .build();

        data.add(newSchool);
        return newSchool;
    }

    @Override
    public School getById(Long id) {
        return data.stream()
            .filter(school -> school.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new GroupNotFoundException(GROUPID_NOT_FOUND_GROUP_EXCEPTION));
    }

    @Override
    public Optional<School> findById(Long id) {
        return data.stream()
            .filter(school -> school.getId().equals(id))
            .findFirst();
    }

    @Override
    public Integer countDistinctSchoolNameContaining(String schoolName) {
        return null;
    }

    @Override
    public List<String> findDistinctSchoolNameContaining(String schoolName, Pageable pageable) {
        return null;
    }

    @Override
    public Integer countAllBySchoolNameContaining(String schoolName, String departmentName) {
        return null;
    }

    @Override
    public List<School> findAllBySchoolNameContaining(String schoolName, String departmentName,
        Pageable pageable) {
        return null;
    }
}
