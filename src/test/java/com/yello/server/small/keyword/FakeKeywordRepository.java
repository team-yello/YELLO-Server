package com.yello.server.small.keyword;

import com.yello.server.domain.keyword.entity.Keyword;
import com.yello.server.domain.keyword.repository.KeywordRepository;
import java.util.ArrayList;
import java.util.List;

public class FakeKeywordRepository implements KeywordRepository {

    private final List<Keyword> data = new ArrayList<>();
    private Long id = 0L;

    @Override
    public Keyword save(Keyword keyword) {
        Keyword newKeyword = Keyword.builder()
            .id(keyword.getId() == null ? id++ : keyword.getId())
            .keywordName(keyword.getKeywordName())
            .question(keyword.getQuestion())
            .build();
        data.add(newKeyword);
        return newKeyword;
    }

    @Override
    public List<Keyword> findAll() {
        return data.stream().toList();
    }
}
