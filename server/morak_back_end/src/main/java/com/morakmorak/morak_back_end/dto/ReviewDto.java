package com.morakmorak.morak_back_end.dto;

import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;

public class ReviewDto {
    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestReview {
        private String content;
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response {
        private Long reviewId;
        private String content;
        private LocalDateTime createdAt;
        private UserDto.ResponseSimpleUserDto userInfo;

        @QueryProjection
        public Response(Long reviewId, String content, LocalDateTime createdAt, Long userId, String nickname, Grade grade) {
            this.reviewId = reviewId;
            this.content = content;
            this.createdAt = createdAt;
            this.userInfo = UserDto.ResponseSimpleUserDto.builder()
                    .userId(userId)
                    .nickname(nickname)
                    .grade(grade)
                    .build();
        }

        public Response(Long reviewId, String content, LocalDateTime createdAt, UserDto.ResponseSimpleUserDto userInfo) {
            this.reviewId = reviewId;
            this.content = content;
            this.createdAt = createdAt;
            this.userInfo = userInfo;
        }
    }
}
