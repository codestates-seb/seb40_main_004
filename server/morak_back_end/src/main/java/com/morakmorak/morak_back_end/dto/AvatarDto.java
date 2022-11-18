package com.morakmorak.morak_back_end.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.morakmorak.morak_back_end.entity.Avatar;
import lombok.*;

public class AvatarDto {
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SimpleResponse {
        private Long avatarId;
        private String fileName;
        private String remotePath;

        public static SimpleResponse of(Avatar avatar) {
            return avatar == null ? SimpleResponse.builder()
                    .avatarId(null)
                    .fileName(null)
                    .remotePath(null)
                    .build()
                    :
                    SimpleResponse.builder()
                            .avatarId(avatar.getId())
                            .fileName(avatar.getOriginalFileName())
                            .remotePath(avatar.getRemotePath())
                            .build();
        }
    }
}