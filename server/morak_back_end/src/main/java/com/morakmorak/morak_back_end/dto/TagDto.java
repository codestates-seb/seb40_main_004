package com.morakmorak.morak_back_end.dto;

import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.service.EnumValid;
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
//        @EnumValid
        private TagName name;

        @QueryProjection
        public SimpleTag(Long tagId, TagName name) {
            this.tagId = tagId;
            this.name = name;
        }
    }
}
