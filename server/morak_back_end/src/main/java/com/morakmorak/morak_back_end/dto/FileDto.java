package com.morakmorak.morak_back_end.dto;

import lombok.*;

public class FileDto {


    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor (access = AccessLevel.PROTECTED)
    public static class RequestFileWithId {
        private Long fileId;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor (access = AccessLevel.PROTECTED)
    public static class ResponseFileDto {
        private Long fileId;
        private String preSignedUrl;
    }
}
