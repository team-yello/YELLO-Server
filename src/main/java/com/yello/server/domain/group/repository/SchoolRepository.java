package com.yello.server.domain.group.repository;

import com.yello.server.domain.group.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Long> {
}
