package com.morakmorak.morak_back_end.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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

        @Getter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class Detail {
                Long total;
                List<Article> articles;
                List<Article> answers;
                List<Comment> comments;
        }

        @Getter
        @Builder
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class Article {
                Long articleId;
                String title;
                Long likeCount;
                Long commentCount;
                LocalDate createdDate;

                @QueryProjection
                public Article(Long articleId, String title, Long likeCount, Long commentCount, LocalDate createdDate) {
                        this.articleId = articleId;
                        this.title = title;
                        this.likeCount = likeCount;
                        this.commentCount = commentCount;
                        this.createdDate = createdDate;
                }
        }

        @Getter
        @Builder
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class Comment {
                Long articleId;
                String content;
                LocalDate createdDate;

                @QueryProjection
                public Comment(Long articleId, String content, LocalDate createdDate) {
                        this.articleId = articleId;
                        this.content = content;
                        this.createdDate = createdDate;
                }
        }
}
