package com.yello.server.global.common.util;

import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    public static <T> Page<T> getPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        List<T> pageList = (start <= end) ? list.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(pageList, pageable, pageList.size());
    }
}
