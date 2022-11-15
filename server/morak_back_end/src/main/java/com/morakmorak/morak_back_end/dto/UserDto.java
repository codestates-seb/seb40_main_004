package com.morakmorak.morak_back_end.dto;


import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import lombok.*;

import java.util.ArrayList;
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

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class User {
        private Long userId;
        private String nickname;
        private Grade grade;
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
    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseForCommentUserInfo {
        private Long userId;
        private String nickname;
//        private AvatarDto.SimpleResponse avatar;(추가 여부 논의 필요)
        //mapper 변경 가능 부분, 재사용성을 높이려면 ?? comment가 삭제됐을 경우 상태관리 how??
        public static ResponseForCommentUserInfo of(User userFromComment) {
            return ResponseForCommentUserInfo.builder()
                    .userId(userFromComment.getUserId())
                    .nickname(userFromComment.getNickname())
                    .build();
        }

    }

}
