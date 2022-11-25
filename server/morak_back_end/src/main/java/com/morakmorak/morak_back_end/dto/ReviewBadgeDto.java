package com.morakmorak.morak_back_end.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import javax.validation.constraints.NotBlank;

public class ReviewBadgeDto {
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SimpleReviewBadgeDto {
        @NotBlank
        private Long reviewBadgeId;
        private String name;

        @QueryProjection
        public SimpleReviewBadgeDto(Long reviewBadgeId, String name) {
            this.reviewBadgeId = reviewBadgeId;
            this.name = name;
        }
    }
}
