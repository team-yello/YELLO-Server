package com.yello.server.global.common.factory;

import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationFactory {

    private static final int PAGE_LIMIT = 100;
    private static final int PAGE_LIMIT_TEN = 10;

    private PaginationFactory() {
        throw new IllegalStateException();
    }

    public static Pageable createPageable(Integer page) {
        return PageRequest.of(page, PAGE_LIMIT);
    }

    public static Pageable createPageable(Integer page, Integer limit) {
        return PageRequest.of(page, limit);
    }

    public static Pageable createPageableLimitTen(Integer page) {
        return createPageable(page, PAGE_LIMIT_TEN);
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
