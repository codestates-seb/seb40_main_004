package com.morakmorak.morak_back_end.dto;


import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.JobType;
import com.morakmorak.morak_back_end.service.EnumValid;
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
        @EnumValid
        private Grade grade;
        public static ResponseSimpleUserDto of(User userFromComment) {
            return ResponseSimpleUserDto.builder()
                    .userId(userFromComment.getId())
                    .nickname(userFromComment.getNickname())
                    .grade(userFromComment.getGrade())
                    .build();
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SimpleEditProfile {
        @NotBlank
        @Pattern(regexp = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣].{2,16}$", message = INVALID_NICKNAME)// 영문, 숫자, 한글 2자 이상 16자 이하(공백 및 초성, 자음 불가능)
        private String nickname;
        private String infoMessage;
        private String github;
        private String blog;
        @EnumValid
        private JobType jobType;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseRanking {
        private Long userId;
        private String nickname;
        private String infoMessage;
        private Integer point;
        private Grade grade;
        private JobType jobType;
        private Long articleCount;
        private Long likeCount;
        private Long answerCount;
        private Integer rank;
        private AvatarDto.SimpleResponse avatar;

        @QueryProjection
        public ResponseRanking(Long userId, String nickname, String infoMessage, Integer point, Grade grade, JobType jobType, Long articleCount, Long likeCount, Long answerCount, Long avatarId, String filename, String remotePath) {
            this.userId = userId;
            this.nickname = nickname;
            this.infoMessage = infoMessage;
            this.point = point;
            this.grade = grade;
            this.jobType = jobType;
            this.articleCount = articleCount;
            this.likeCount = likeCount;
            this.answerCount = answerCount;
            this.avatar = AvatarDto.SimpleResponse
                    .builder()
                    .avatarId(avatarId)
                    .filename(filename)
                    .remotePath(remotePath)
                    .build();
        }
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
        @EnumValid
        private Grade grade;
        private Integer point;
        private String github;
        private String blog;
        private String infoMessage;

        private AvatarDto.SimpleResponse avatar;
        private List<TagQueryDto> tags;
        private List<BadgeQueryDto> reviewBadges;
        private List<ArticleDto.ResponseListTypeArticle> articles;
        private List<ActivityQueryDto> activities;
        private List<ReviewDto.Response> reviews;

        @QueryProjection
        public ResponseDashBoard(Long userId, String email, String nickname, JobType jobType, Grade grade, Integer point, String github, String blog, Long avatarId, String remotePath, String filename) {
            this.userId = userId;
            this.email = email;
            this.nickname = nickname;
            this.jobType = jobType;
            this.grade = grade;
            this.point = point;
            this.github = github;
            this.blog = blog;
            this.avatar = AvatarDto.SimpleResponse.builder()
                    .avatarId(avatarId)
                    .remotePath(remotePath)
                    .filename(filename)
                    .build();
        }

        public void addRestInfo(List<TagQueryDto> tags, List<BadgeQueryDto> reviewBadges, List<ArticleDto.ResponseListTypeArticle> articles, List<ActivityQueryDto> activities, List<ReviewDto.Response> reviews) {
            this.tags = tags;
            this.reviewBadges = reviewBadges;
            this.articles = articles;
            this.activities = activities;
            this.reviews = reviews;
        }
    }
}
