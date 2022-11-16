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

        @QueryProjection
        public ActivityDto(Long articleCount, Long answerCount, Long commentCount,String createdAt) {
                this.articleCount = articleCount;
                this.answerCount = answerCount;
                this.commentCount = commentCount;
                this.total = articleCount + answerCount + commentCount;
                this.createdDate = createdAt;
        }

        @Builder
        public ActivityDto(Long articleCount, Long answerCount, Long commentCount, Long total, LocalDate createdDate) {
                this.articleCount = articleCount;
                this.answerCount = answerCount;
                this.commentCount = commentCount;
                this.total = total;
                this.createdDate = createdDate.toString();
        }
}
