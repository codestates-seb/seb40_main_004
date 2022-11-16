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
        private String fileName;//컨벤션 확인
        private String remotePath;

        //mapper 적용 가능 부분
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
        

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {
        private Long avatarId;
        private String remotePath;
        private String filename;

        @QueryProjection
        public Request(Long avatarId, String remotePath, String filename) {
            this.avatarId = avatarId;
            this.remotePath = remotePath;
            this.filename = filename;
        }
    }
}
