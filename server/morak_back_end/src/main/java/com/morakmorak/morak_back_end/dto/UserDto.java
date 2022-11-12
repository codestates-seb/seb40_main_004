package com.morakmorak.morak_back_end.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.morakmorak.morak_back_end.dto.DtoValidConstants.INVALID_NICKNAME;

public class UserDto {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserInfo {
        private String email;
        private Long id;
        private List<String> roles;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestEditProfile {
        @NotBlank
        @Pattern(regexp = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣].{2,16}$", message = INVALID_NICKNAME) // 영문, 숫자, 한글 2자 이상 16자 이하(공백 및 초성, 자음 불가능)
        private String nickname;
        @NotBlank
        private String introduce;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseDashBoard {
        private String nickname;
    }
}
