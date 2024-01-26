package com.yello.server.domain.notice.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yello.server.domain.notice.entity.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.yello.server.domain.notice.entity.QNotice.notice;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeRepositoryImpl implements NoticeRepository {

    private final NoticeJpaRepository noticeJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Notice getTopNotice() {
        return jpaQueryFactory
                .selectFrom(notice)
                .orderBy(notice.endDate.desc())
                .fetchFirst();
    }

    @Override
    public Notice save(Notice notice) {
        return null;
    }

}
