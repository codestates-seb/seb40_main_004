package com.morakmorak.morak_back_end.dto;

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
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response {
        private Long reviewId;
        private String content;
        private LocalDateTime createdAt;
        private UserDto.ResponseSimpleUserDto userInfo;
    }
}
