package com.morakmorak.morak_back_end.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

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
    public static class ReponsePost {
        private UserDto.ResponseForCommentUserInfo userInfo;
        private AvatarDto.SimpleResponse avatar;
        private Long commentId;
        private Long articleId;
        private String content;
    }
}
