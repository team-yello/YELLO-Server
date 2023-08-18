package com.yello.server.global.common.repository;

import com.yello.server.global.common.entity.GoogleToken;
import java.util.Optional;

public interface GoogleTokenRepository {

    Long tokenId = 1L;

    GoogleToken save(GoogleToken token);

    GoogleToken getById(Long id);

    Optional<GoogleToken> findById(Long id);
}
