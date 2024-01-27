package com.yello.server.domain.notice.repository;

import static com.yello.server.domain.notice.entity.QNotice.notice;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yello.server.domain.notice.entity.Notice;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeRepositoryImpl implements NoticeRepository {

    private final NoticeJpaRepository noticeJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Notice getTopNotice() {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        return jpaQueryFactory
            .selectFrom(notice)
            .where(notice.isAvailable.eq(true)
                .and(notice.startDate.loe(zonedDateTime))
                .and(notice.endDate.goe(zonedDateTime)))
            .orderBy(notice.endDate.desc())
            .fetchFirst();
    }

    @Override
    public Notice save(Notice notice) {
        return noticeJpaRepository.save(notice);
    }

}
