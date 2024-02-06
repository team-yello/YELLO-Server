package com.yello.server.domain.notice.repository;

import com.yello.server.domain.notice.entity.Notice;
import com.yello.server.domain.notice.entity.NoticeType;
import java.util.List;
import java.util.Optional;

public interface NoticeRepository {

    Optional<Notice> findTopNotice(NoticeType tag);

    List<Notice> findAll();

    Notice getById(Long id);

    Notice save(Notice notice);

    void update(Notice newNotice);
}
