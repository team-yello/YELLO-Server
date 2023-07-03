package com.yello.server.domain.vote.controller;

import static com.yello.server.global.common.SuccessCode.READ_VOTE_SUCCESS;
import static com.yello.server.global.common.util.ConstantUtil.PAGE_LIMIT;

import com.yello.server.domain.vote.service.VoteServiceImpl;
import com.yello.server.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteServiceImpl voteService;

    //todo Swagger
    @GetMapping
    public ResponseEntity<ApiResponse> findAllMyVotes(@RequestParam Integer page) {
        Pageable pageable = PageRequest.of(page, PAGE_LIMIT);
        val data = voteService.findAllVotes(pageable);
        return ResponseEntity.ok(ApiResponse.success(READ_VOTE_SUCCESS, data));
    }
}
