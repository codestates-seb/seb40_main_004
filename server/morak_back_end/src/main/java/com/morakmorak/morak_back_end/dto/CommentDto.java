package com.morakmorak.morak_back_end.dto;

import com.morakmorak.morak_back_end.entity.Comment;
import lombok.*;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class CommentDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {
        @NotBlank
        public String content;
    }


    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response {
        private Long commentId;
        private Long articleId;
        private Long answerId;
        private String content;
        private UserDto.ResponseSimpleUserDto userInfo;
        private AvatarDto.SimpleResponse avatar;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;

        public static CommentDto.Response ofArticle(Optional<Comment> savedComment) {
            if (savedComment.isPresent()) {
                return CommentDto.Response.builder()
                        .userInfo(UserDto.ResponseSimpleUserDto.of(savedComment.get().getUser()))
                        .avatar(AvatarDto.SimpleResponse.of(savedComment.get().getUser().getAvatar()))
                        .commentId(savedComment.get().getId())
                        .articleId(savedComment.get().getArticle().getId())
                        .content(savedComment.get().getContent())
                        .createdAt(savedComment.get().getCreatedAt())
                        .lastModifiedAt(savedComment.get().getLastModifiedAt())
                        .build();
            } else {
                return Response.builder().build();
            }
        }
        public static CommentDto.Response ofAnswer(Comment savedComment) {

                return Response.builder()
                        .userInfo(UserDto.ResponseSimpleUserDto.of(savedComment.getUser()))
                        .avatar(AvatarDto.SimpleResponse.of(savedComment.getUser().getAvatar()))
                        .commentId(savedComment.getId())
                        .answerId(savedComment.getAnswer().getId())
                        .content(savedComment.getContent())
                        .createdAt(savedComment.getCreatedAt())
                        .lastModifiedAt(savedComment.getLastModifiedAt())
                        .build();

        }
        public static CommentDto.Response previewOfAnswer(List<Comment> commentsFromAnswer) {
            if (!ObjectUtils.isEmpty(commentsFromAnswer)) {
                Comment savedComment = commentsFromAnswer.get(0);
                return Response.builder()
                        .userInfo(UserDto.ResponseSimpleUserDto.of(savedComment.getUser()))
                        .avatar(AvatarDto.SimpleResponse.of(savedComment.getUser().getAvatar()))
                        .commentId(savedComment.getId())
                        .answerId(savedComment.getAnswer().getId())
                        .content(savedComment.getContent())
                        .createdAt(savedComment.getCreatedAt())
                        .lastModifiedAt(savedComment.getLastModifiedAt())
                        .build();
            } else {
                return Response.builder().build();
            }

        }


    }
}