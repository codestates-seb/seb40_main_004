package com.morakmorak.morak_back_end.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityDto {
        Long articleCount;
        Long answerCount;
        Long commentCount;
        Long total;
        String createdDate;

        @Builder
        public ActivityDto(Long articleCount, Long answerCount, Long commentCount, Long total, LocalDate createdDate) {
                this.articleCount = articleCount;
                this.answerCount = answerCount;
                this.commentCount = commentCount;
                this.total = total;
                this.createdDate = createdDate.toString();
        }

        @Getter
        @Builder
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class Temporary {
                Long count;
                LocalDate createdDate;

                @QueryProjection
                public Temporary(Long count, LocalDate createdDate) {
                        this.count = count;
                        this.createdDate = createdDate;
                }
        }

        @Getter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class Response {
                Long articleCount;
                Long answerCount;
                Long commentCount;
                Long total;
                LocalDate createdDate;
        }
}
