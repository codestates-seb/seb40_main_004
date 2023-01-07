package com.morakmorak.morak_back_end.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.morakmorak.morak_back_end.entity.Avatar;
import lombok.*;

import java.io.Serializable;

public class AvatarDto {
    @Getter
    @Builder
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SimpleResponse implements Serializable {
        private Long avatarId;
        private String filename;
        private String remotePath;

        public static SimpleResponse of(Avatar avatar) {
            return avatar == null ? SimpleResponse.builder()
                    .avatarId(null)
                    .filename(null)
                    .remotePath(null)
                    .build()
                    :
                    SimpleResponse.builder()
                            .avatarId(avatar.getId())
                            .filename(avatar.getOriginalFilename())
                            .remotePath(avatar.getRemotePath())
                            .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ResponseS3Url {
        private Long avatarId;
        private String preSignedUrl;
    }
}