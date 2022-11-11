package com.morakmorak.morak_back_end.dto;

import lombok.*;

public class TagDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestTagWithIdAndName {
        private Long tagId;
        private String tagName;
    }
}
