package com.morakmorak.morak_back_end.dto;

import lombok.*;

public class AvatarDto {
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {
        private Long avatarId;
        private String remotePath;
        private String filename;
    }
}
