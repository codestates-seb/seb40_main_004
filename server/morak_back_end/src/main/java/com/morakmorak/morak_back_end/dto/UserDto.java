package com.morakmorak.morak_back_end.dto;


import com.morakmorak.morak_back_end.entity.enums.Grade;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class User {
        private Long userId;
        private String nickname;
        private Grade grade;
    }


}
