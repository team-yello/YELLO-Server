package com.yello.server.domain.group.repository;

import com.yello.server.domain.group.entity.School;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface SchoolRepository {

    School findById(Long id);

    Integer countDistinctSchoolNameContaining(String schoolName);

    List<String> findDistinctSchoolNameContaining(String schoolName, Pageable pageable);

    Integer countAllBySchoolNameContaining(String schoolName, String departmentName);

    List<School> findAllBySchoolNameContaining(String schoolName, String departmentName,
        Pageable pageable);
}
