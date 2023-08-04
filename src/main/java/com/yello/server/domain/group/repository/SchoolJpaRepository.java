package com.yello.server.domain.group.repository;

import com.yello.server.domain.group.entity.School;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SchoolJpaRepository extends JpaRepository<School, Long> {

    @Query("select count (DISTINCT s.schoolName)from School s " +
        "where s.schoolName " +
        "like CONCAT('%',:schoolName,'%')")
    Integer countDistinctSchoolNameContaining(@Param("schoolName") String schoolName);

    @Query("select distinct(s.schoolName) from School s " +
        "where s.schoolName " +
        "like CONCAT('%',:schoolName,'%')")
    List<String> findDistinctSchoolNameContaining(@Param("schoolName") String schoolName,
        Pageable pageable);

    @Query("select count(s) from School s " +
        "where s.schoolName = :schoolName and s.departmentName " +
        "like CONCAT('%',:departmentName,'%')")
    Integer countAllBySchoolNameContaining(@Param("schoolName") String schoolName,
        @Param("departmentName") String departmentName);

    @Query("select s from School s " +
        "where s.schoolName = :schoolName and s.departmentName " +
        "like CONCAT('%',:departmentName,'%')")
    List<School> findAllBySchoolNameContaining(@Param("schoolName") String schoolName,
        @Param("departmentName") String departmentName, Pageable pageable);
}
