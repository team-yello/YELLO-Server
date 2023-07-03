package com.yello.server.domain.keyword.repository;

import com.yello.server.domain.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}
