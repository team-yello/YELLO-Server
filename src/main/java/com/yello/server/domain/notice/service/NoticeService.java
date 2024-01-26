package com.yello.server.domain.notice.service;

import com.yello.server.domain.notice.dto.NoticeDataResponse;
import com.yello.server.domain.notice.entity.Notice;
import com.yello.server.domain.notice.repository.NoticeRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.yello.server.global.common.factory.TimeFactory.compareNowAndEndData;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    public NoticeDataResponse findNotice(Long userId) {
        final User user = userRepository.getById(userId);
        Notice noticeData = noticeRepository.getTopNotice();
        return NoticeDataResponse.of(noticeData, compareNowAndEndData(noticeData.getEndDate()));
    }


}
