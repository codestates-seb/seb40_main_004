package com.morakmorak.morak_back_end.controller.utility;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

public class PageRequestGenerator {
    private static final Integer MAX_SIZE = 50;

    public static PageRequest of(String sort, Integer page, Integer size) {
        Sort s = (!StringUtils.hasText(sort)) ? Sort.by("point") : Sort.by(sort);
        page = Math.max(page-1, 0);
        size = Math.min(size, MAX_SIZE);

        return PageRequest.of(page, size, s);
    }

    public static PageRequest of(Integer page, Integer size) {
        page = Math.max(page-1, 0);
        size = Math.min(size, MAX_SIZE);

        return PageRequest.of(page, size);
    }
}
