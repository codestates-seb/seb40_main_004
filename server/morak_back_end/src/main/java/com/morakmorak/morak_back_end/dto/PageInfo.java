package com.morakmorak.morak_back_end.dto;

import lombok.*;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PageInfo implements Serializable {
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Sort sort;
}
