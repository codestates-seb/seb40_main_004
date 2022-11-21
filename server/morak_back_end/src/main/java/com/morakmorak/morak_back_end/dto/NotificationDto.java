package com.morakmorak.morak_back_end.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationDto {

    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SimpleResponse {
        private Long notificationId;
        private String message;
        private Boolean isChecked;
        private LocalDateTime createdAt;

        @QueryProjection
        public SimpleResponse(Long notificationId, String message, Boolean isChecked, LocalDateTime createdAt) {
            this.notificationId = notificationId;
            this.message = message;
            this.isChecked = isChecked;
            this.createdAt = createdAt;
        }
    }
}
