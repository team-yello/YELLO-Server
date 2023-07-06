package com.yello.server.global.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationUtil {

    private static final int PAGE_LIMIT = 10;

    public static Pageable createPageable(Integer page) {
        return PageRequest.of(page, PAGE_LIMIT);
    }
}
