package com.morakmorak.morak_back_end.dto;

import lombok.*;
import org.springframework.data.domain.Sort;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PageInfo {
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Sort sort;
}
