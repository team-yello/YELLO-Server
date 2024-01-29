package com.yello.server.domain.user.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yello.server.domain.user.entity.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDataRepositoryImpl implements UserDataRepository{

    private final UserDataJpaRepository userDataJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public UserData save(UserData userData) {
        return userDataJpaRepository.save(userData);
    }
}
