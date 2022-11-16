package com.morakmorak.morak_back_end.dto;


import com.morakmorak.morak_back_end.entity.ReviewBadge;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.JobType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import javax.validation.constraints.NotBlank;
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
    public static class ResponseSimpleUserDto {
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
        private Long userId;
        private String email;
        private String nickname;
        private JobType jobType;
        private Grade grade;
        private Integer point;
        private String github;
        private String blog;

        private AvatarDto.Request avatar;
        private List<TagDto.SimpleTag> tags;
        private List<ReviewBadgeDto.SimpleReviewBadgeDto> reviewBadges;
        private List<ArticleDto.ResponseListTypeArticle> articles;
        private List<ActivityDto> activities;
        private List<ReviewDto.Response> reviews;

        @QueryProjection
        public ResponseDashBoard(Long userId, String email, String nickname, JobType jobType, Grade grade, Integer point, String github, String blog) {
            this.userId = userId;
            this.email = email;
            this.nickname = nickname;
            this.jobType = jobType;
            this.grade = grade;
            this.point = point;
            this.github = github;
            this.blog = blog;
        }
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
        public static ResponseForCommentUserInfo of(com.morakmorak.morak_back_end.entity.User userFromComment) {
            return ResponseForCommentUserInfo.builder()
                    .userId(userFromComment.getId())
                    .nickname(userFromComment.getNickname())
                    .build();
        }

    }

}
