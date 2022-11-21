package com.morakmorak.morak_back_end.dto;

import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.Comment;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnswerDto {
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
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
    public static class RequestUpdateAnswer {
        private String content;
        @Builder.Default
        private List<FileDto.RequestFileWithId> fileIdList = new ArrayList<>();
    }
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
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
        private UserDto.ResponseSimpleUserDto userInfo;
        private AvatarDto.SimpleResponse avatar;
        private String content;
        private LocalDateTime createdAt;
        private Boolean isPicked;
        private Integer answerLikeCount;
        private CommentDto.ResponseForAnswer commentPreview;
        private Integer commentCount;

        public static ResponseListTypeAnswer of(Answer answer) {
            return ResponseListTypeAnswer.builder()
                    .answerId(answer.getId())
                    .userInfo(UserDto.ResponseSimpleUserDto.of(answer.getUser()))
                    .avatar(AvatarDto.SimpleResponse.of(answer.getUser().getAvatar()))
                    .content(answer.getContent())
                    .createdAt(answer.getCreatedAt())
                    .isPicked(answer.getIsPicked())
                    .answerLikeCount(answer.getAnswerLike().size())
                    .commentPreview(CommentDto.ResponseForAnswer.of(answer.getComments()))
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

}
