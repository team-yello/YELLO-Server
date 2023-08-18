package com.yello.server.global.common.repository;

import static com.yello.server.global.common.ErrorCode.GOOGLE_TOKEN_NOT_FOUND_EXCEPTION;

import com.yello.server.domain.purchase.exception.GoogleTokenNotFoundException;
import com.yello.server.global.common.entity.GoogleToken;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GoogleTokenRepositoryImpl implements GoogleTokenRepository {

    private final GoogleTokenJpaRepository googleTokenJpaRepository;

    @Override
    public GoogleToken save(GoogleToken token) {
        return googleTokenJpaRepository.save(token);
    }

    @Override
    public GoogleToken getById(Long id) {
        return googleTokenJpaRepository.findById(id)
            .orElseThrow(() -> new GoogleTokenNotFoundException(GOOGLE_TOKEN_NOT_FOUND_EXCEPTION));
    }

    @Override
    public Optional<GoogleToken> findById(Long id) {
        return googleTokenJpaRepository.findById(id);
    }
}
