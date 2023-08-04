package com.yello.server.domain.keyword.repository;

import com.yello.server.domain.keyword.entity.Keyword;
import java.util.List;

public interface KeywordRepository {

    Keyword save(Keyword keyword);

    List<Keyword> findAll();

}
