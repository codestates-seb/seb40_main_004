package com.morakmorak.morak_back_end.dto;

import lombok.*;

import java.time.LocalDate;

public class ActivityDto {
    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestActivity {
        Integer articles;
        Integer answers;
        Integer comments;
        Integer total;
        LocalDate createdDate;
    }
}
