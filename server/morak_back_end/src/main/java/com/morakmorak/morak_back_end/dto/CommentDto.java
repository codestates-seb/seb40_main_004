package com.morakmorak.morak_back_end.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class CommentDto {
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestPost{
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
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private UserDto.ResponseSimpleUserDto userInfo;
        private AvatarDto.SimpleResponse avatar;
    }
}
