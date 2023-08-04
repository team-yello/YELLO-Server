package com.yello.server.domain.keyword.repository;

import com.yello.server.domain.keyword.entity.Keyword;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KeywordRepositoryImpl implements KeywordRepository {

    private final KeywordJpaRepository keywordJpaRepository;

    @Override
    public Keyword save(Keyword keyword) {
        return keywordJpaRepository.save(keyword);
    }

    @Override
    public List<Keyword> findAll() {
        return keywordJpaRepository.findAll();
    }
}
