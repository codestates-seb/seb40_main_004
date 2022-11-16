package com.morakmorak.morak_back_end.dto;

import com.morakmorak.morak_back_end.entity.Avatar;
import lombok.*;

public class AvatarDto {
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseAvatarInfo {
        private Long avatarId;
        private String fileName;//컨벤션 확인
        private String remotePath;

        //mapper 적용 가능 부분
        public static ResponseAvatarInfo of(Avatar avatar) {
            return avatar == null ? ResponseAvatarInfo.builder()
                    .avatarId(null)
                    .fileName(null)
                    .remotePath(null)
                    .build()
                    :
                    ResponseAvatarInfo.builder()
                            .avatarId(avatar.getId())
                            .fileName(avatar.getOriginalFileName())
                            .remotePath(avatar.getRemotePath())
                            .build();
        }
    }
}

