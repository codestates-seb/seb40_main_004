package com.morakmorak.morak_back_end.dto;

import lombok.*;

import java.time.LocalDateTime;

public class BookmarkDto {
    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponsePostBookmark {

        private Long articleId;

        private Long userId;

        private boolean scrappedByThisUser;

        private LocalDateTime createdAt;

        private LocalDateTime lastModifiedAt;

    }
}
