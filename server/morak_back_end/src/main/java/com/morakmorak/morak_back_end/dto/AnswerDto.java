package com.morakmorak.morak_back_end.dto;

import com.morakmorak.morak_back_end.entity.Answer;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnswerDto {
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class RequestPostAnswer {
        @NotBlank
        @Length(min = 15)
        private String content;
        private List<FileDto.RequestFileWithId> fileIdList = new ArrayList<>();
    }
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class RequestUpdateAnswer {
        private String content;
        @Builder.Default
        private List<FileDto.RequestFileWithId> fileIdList = new ArrayList<>();
    }
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class SimpleResponsePostAnswer {
        private Long answerId;
        private UserDto.ResponseSimpleUserDto userInfo;
        private AvatarDto.SimpleResponse avatar;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private Integer commentCount;
        public static SimpleResponsePostAnswer of(Answer savedAnswer) {
            return SimpleResponsePostAnswer.builder()
                    .answerId(savedAnswer.getId())
                    .userInfo(UserDto.ResponseSimpleUserDto.of(savedAnswer.getUser()))
                    .avatar(AvatarDto.SimpleResponse.of(savedAnswer.getUser().getAvatar()))
                    .content(savedAnswer.getContent())
                    .createdAt(savedAnswer.getCreatedAt())
                    .lastModifiedAt(savedAnswer.getLastModifiedAt())
                    .commentCount(savedAnswer.getComments().size())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseListTypeAnswer {
        private Long answerId;
        private String content;
        private Boolean isPicked;
        private Integer answerLikeCount;
        private Integer commentCount;
        private LocalDateTime createdAt;
        private Boolean isLiked = false;
        private UserDto.ResponseSimpleUserDto userInfo;
        private AvatarDto.SimpleResponse avatar;
        private CommentDto.Response commentPreview;

        public static ResponseListTypeAnswer of(Answer answer) {
            return ResponseListTypeAnswer.builder()
                    .answerId(answer.getId())
                    .userInfo(UserDto.ResponseSimpleUserDto.of(answer.getUser()))
                    .avatar(AvatarDto.SimpleResponse.of(answer.getUser().getAvatar()))
                    .content(answer.getContent())
                    .createdAt(answer.getCreatedAt())
                    .isPicked(answer.getIsPicked())
                    .isLiked(false)
                    .answerLikeCount(answer.getAnswerLike().size())
                    .commentPreview(CommentDto.Response.previewOfAnswer(answer.getComments()))
                    .commentCount(answer.getComments().size())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseAnswerLike {
        private Long answerId;
        private Long userId;
        private Boolean isLiked;
        private Integer likeCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseUserAnswerList {
        private Long answerId;
        private String content;
        private Boolean isPicked;
        private Integer answerLikeCount;
        private Integer commentCount;
        private LocalDateTime createdAt;
        private UserDto.ResponseSimpleUserDto userInfo;
    }
}
