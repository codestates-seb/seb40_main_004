package com.morakmorak.morak_back_end.dto;

import com.morakmorak.morak_back_end.entity.Comment;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

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
        private String content;
        private UserDto.ResponseSimpleUserDto userInfo;
        private AvatarDto.SimpleResponse simpleResponse;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        
        public static CommentDto.Response of(Comment savedComment) {
            return CommentDto.Response.builder()
                    .userInfo(UserDto.ResponseSimpleUserDto.of(savedComment.getUser()))
                    .simpleResponse(AvatarDto.SimpleResponse.of(savedComment.getUser().getAvatar()))
                    .commentId(savedComment.getId())
                    .articleId(savedComment.getArticle().getId())
                    .content(savedComment.getContent())
                    .createdAt(savedComment.getCreatedAt())
                    .lastModifiedAt(savedComment.getLastModifiedAt())
                    .build();
        }
    }
}
