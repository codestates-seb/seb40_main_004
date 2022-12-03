package com.morakmorak.morak_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.domain.Sort;

@Builder
@AllArgsConstructor
public final class PageRequest {
    private int page;
    private int size;
    private Sort sort;

    private final int MAX_SIZE = 50;

    public void setPage(int page) {
        this.page = Math.min(page-1, 0);
    }

    public void setSize(int size) {
        this.size = Math.min(size, MAX_SIZE);
    }

    public void addSort(String sort) {
        this.sort = Sort.by(sort);
    }

    public org.springframework.data.domain.PageRequest toRealPageRequest() {
        return org.springframework.data.domain.PageRequest.of(page, size, sort);
    }
}
