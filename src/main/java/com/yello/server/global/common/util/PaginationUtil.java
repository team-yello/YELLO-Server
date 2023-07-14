package com.yello.server.global.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtil {

    private static final int PAGE_LIMIT = 10;

    public static Pageable createPageable(Integer page) {
        return PageRequest.of(page, PAGE_LIMIT);
    }

    public static Pageable createPageableByNameSort(Integer page) {
        return PageRequest.of(page, PAGE_LIMIT, Sort.by(Sort.Direction.ASC, "name"));
    }
}
