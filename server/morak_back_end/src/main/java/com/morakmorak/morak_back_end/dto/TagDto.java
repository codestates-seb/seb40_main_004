package com.morakmorak.morak_back_end.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import javax.validation.constraints.NotBlank;

public class TagDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SimpleTag {
        @NotBlank
        private Long tagId;
        private String name;

        @QueryProjection
        public SimpleTag(Long tagId, String name) {
            this.tagId = tagId;
            this.name = name;
        }
    }
}
