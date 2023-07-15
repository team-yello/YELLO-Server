package com.yello.server.domain.group.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchoolRepository extends JpaRepository<School, Long> {
    // Create

    // Read
    List<School> findAllBySchoolNameAndDepartmentName(String schoolName, String departmentName);

    // Update

    // Delete
}
