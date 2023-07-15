package com.yello.server.global.common.util;

import org.springframework.data.domain.*;

import java.util.List;

public class PaginationUtil {

    private static final int PAGE_LIMIT = 10;

    public static Pageable createPageable(Integer page) {
        return PageRequest.of(page, PAGE_LIMIT);
    }

    public static Pageable createPageableByNameSort(Integer page) {
        return PageRequest.of(page, PAGE_LIMIT, Sort.by(Sort.Direction.ASC, "name"));
    }

    public static <T> Page<T> getPage(List<T> list, Pageable pageable) {
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }
}
