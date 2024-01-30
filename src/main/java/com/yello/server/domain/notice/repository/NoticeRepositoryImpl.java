package com.yello.server.domain.notice.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yello.server.domain.notice.entity.Notice;
import com.yello.server.domain.notice.entity.NoticeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static com.yello.server.domain.notice.entity.QNotice.notice;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeRepositoryImpl implements NoticeRepository {

    private final NoticeJpaRepository noticeJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Notice> findTopNotice(NoticeType tag) {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(notice)
                .where(notice.isAvailable.eq(true)
                        .and(notice.startDate.loe(zonedDateTime))
                        .and(notice.endDate.goe(zonedDateTime))
                        .and(notice.tag.eq(tag)))
                .orderBy(notice.endDate.desc())
                .fetchFirst());
    }

    @Override
    public Notice save(Notice notice) {
        return noticeJpaRepository.save(notice);
    }

}
