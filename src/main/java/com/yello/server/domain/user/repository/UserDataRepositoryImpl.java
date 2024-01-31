package com.yello.server.domain.user.repository;


import static com.yello.server.domain.user.entity.QUserData.userData;
import static com.yello.server.global.common.ErrorCode.USER_DATA_NOT_FOUND_EXCEPTION;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yello.server.domain.user.entity.UserData;
import com.yello.server.domain.user.entity.UserDataType;
import com.yello.server.domain.user.exception.UserDataNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDataRepositoryImpl implements UserDataRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final UserDataJpaRepository userDataJpaRepository;

    @Override
    public UserData save(UserData userData) {
        return userDataJpaRepository.save(userData);
    }

    @Override
    public void update(Long userId, UserDataType tag, String value) {
        jpaQueryFactory.update(userData)
            .set(userData.value, value)
            .where(userData.user.id.eq(userId))
            .where(userData.tag.eq(tag))
            .execute();
    }

    @Override
    public UserData getByUserIdAndTag(Long userId, UserDataType tag) {
        final UserData data = jpaQueryFactory.selectFrom(userData)
            .where(userData.user.id.eq(userId))
            .where(userData.tag.eq(tag))
            .fetchFirst();

        if (data == null) {
            throw new UserDataNotFoundException(USER_DATA_NOT_FOUND_EXCEPTION);
        }

        return data;
    }

    @Override
    public Optional<UserData> findByUserIdAndTag(Long userId, UserDataType tag) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(userData)
            .where(userData.user.id.eq(userId))
            .where(userData.tag.eq(tag))
            .fetchFirst());
    }
}
