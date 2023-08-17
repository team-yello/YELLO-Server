package com.yello.server.global.common.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GoogleTokenRepositoryImpl implements GoogleTokenRepository {

    private final GoogleTokenJpaRepository googleTokenJpaRepository;
}
