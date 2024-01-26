package com.yello.server.domain.notice.repository;

import com.yello.server.domain.notice.entity.Notice;

import java.util.Optional;

public interface NoticeRepository {

    Notice getTopNotice();

}
