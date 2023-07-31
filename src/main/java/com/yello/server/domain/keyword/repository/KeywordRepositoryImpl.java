package com.yello.server.domain.keyword.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KeywordRepositoryImpl implements KeywordRepository {

  private final KeywordJpaRepository keywordJpaRepository;
}
