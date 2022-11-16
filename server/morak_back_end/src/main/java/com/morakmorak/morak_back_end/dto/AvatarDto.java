package com.morakmorak.morak_back_end.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

public class AvatarDto {
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
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
