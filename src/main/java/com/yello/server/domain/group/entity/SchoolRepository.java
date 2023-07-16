package com.yello.server.domain.group.entity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.List;

public interface SchoolRepository extends JpaRepository<School, Long> {
    // Create

    // Read
    @Query("select distinct(s.schoolName) from School s where s.schoolName like CONCAT('%',:schoolName,'%')")
    List<String> findDistinctSchoolNameContaining(@Param("schoolName") String schoolName, Pageable pageable);
    @Query("select s from School s where s.schoolName = :schoolName and s.departmentName like CONCAT('%',:departmentName,'%')")
    List<School> findAllBySchoolNameContaining(@Param("schoolName") String schoolName, @Param("departmentName") String departmentName, Pageable pageable);
    List<School> findAllBySchoolNameAndDepartmentName(String schoolName, String departmentName);

    // Update

    // Delete
}
