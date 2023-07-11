package com.yello.server.domain.question.service;

import com.yello.server.domain.question.dto.response.YelloStartResponse;
import com.yello.server.domain.question.dto.response.YelloVoteResponse;

import java.util.List;

public interface YelloVoteService {
    List<YelloVoteResponse> findYelloVoteList(Long userId);

    YelloStartResponse yelloStartCheck(Long userId);
}
