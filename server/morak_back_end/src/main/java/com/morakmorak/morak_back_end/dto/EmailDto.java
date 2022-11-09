package com.morakmorak.morak_back_end.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.example.boilerplate.dto.DtoValidConstants.INVALID_EMAIL;

public class EmailDto {
    
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RequestSendMail {
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = INVALID_EMAIL)
        private String email;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RequestVerifyAuthKey {
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = INVALID_EMAIL)
        private String email;
        @NotBlank
        private String authKey;
    }
}
