package com.morakmorak.morak_back_end.dto;

import com.morakmorak.morak_back_end.entity.Review;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestPostReview {
        @Size(min = 15)
        private String content;
        private List<BadgeDto.SimpleBadge> badges = new ArrayList<>();
        private Optional<Integer> point;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseDetailReview {

        private Long reviewId;
        private Long articleId;
        private Long answerId;
        private String content;
        private Long senderId;
        private String senderNickname;
        private Integer remainingPoint;
        private Long receiverId;
        private String receiverNickname;
        private LocalDateTime createdAt;
        private List<BadgeDto.SimpleBadge> badges = new ArrayList<>();
        public static ResponseDetailReview of(Review review) {

            ResponseDetailReviewBuilder responseDetailReview = ResponseDetailReview.builder();
            responseDetailReview.content(review.getContent());
            responseDetailReview.remainingPoint(review.getSender().getPoint());
            responseDetailReview.articleId(review.getArticle().getId());
            responseDetailReview.answerId(review.getAnswer().getId());
            responseDetailReview.senderId(review.getSender().getId());
            responseDetailReview.senderNickname(review.getSender().getNickname());
            responseDetailReview.receiverId(review.getReceiver().getId());
            responseDetailReview.receiverNickname(review.getReceiver().getNickname());
            responseDetailReview.reviewId(review.getArticle().getId());
            responseDetailReview.createdAt(review.getCreatedAt());
            responseDetailReview.badges(BadgeDto.SimpleBadge.badgeDtoListFrom(review));
            return responseDetailReview.build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseSimpleReview {
        private Integer senderRemainingPoint;
        private Long reviewId;
        private Long senderId;
        private Long receiverId;
        private LocalDateTime createdAt;
        public static ResponseSimpleReview of(Review review) {
            return ResponseSimpleReview.builder()
                    .senderId(review.getSender().getId())
                    .senderRemainingPoint(review.getSender().getPoint())
                    .reviewId(review.getId())
                    .receiverId(review.getReceiver().getId())
                    .createdAt(review.getCreatedAt())
                    .build();
        }
    }
}
