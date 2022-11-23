package com.morakmorak.morak_back_end.dto;

import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.ReportReason;
import com.morakmorak.morak_back_end.service.EnumValid;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArticleDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestUploadArticle {
        @NotBlank
        @Size(min = 5)
        private String title;
        @Size(min = 5)
        private String content;
        private List<TagDto.SimpleTag> tags = new ArrayList<>();
        private CategoryName category;
        private List<FileDto.RequestFileWithId> fileId = new ArrayList<>();
        private Long thumbnail;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestUpdateArticle {
        @NotBlank
        @Size(min = 15)
        private String title;
        @Size(min = 5)
        private String content;
        @Builder.Default
        private List<TagDto.SimpleTag> tags = new ArrayList<>();
        @Builder.Default
        private List<FileDto.RequestFileWithId> fileId = new ArrayList<>();
        private Long thumbnail;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseSimpleArticle {
        private Long articleId;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseListTypeArticle {
        private Long articleId;
        @EnumValid
        private CategoryName category;
        private String title;
        private Integer clicks;
        private Integer likes;
        private Boolean isClosed;

        private List<TagDto.SimpleTag> tags;

        private Integer commentCount;
        private Integer answerCount;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;

        private UserDto.ResponseSimpleUserDto  userInfo;
        private AvatarDto.SimpleResponse avatar;
    }
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseDetailArticle {
        private Long articleId;
        @EnumValid
        private CategoryName category;
        private String title;
        private String content;
        private Integer clicks;
        private Integer likes;
        private Boolean isClosed;
        private Boolean isLiked;
        private Boolean isBookmarked;
        private List<TagDto.SimpleTag> tags;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private LocalDateTime expiredDate;

        private UserDto.ResponseSimpleUserDto userInfo;
        private AvatarDto.SimpleResponse avatar;

        private List<CommentDto.Response> comments;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseArticleLike {
        private Long articleId;
        private Long userId;
        private Boolean isLiked;
        private Integer likeCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestReportArticle {
        private ReportReason reason;
        private String content;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseReportArticle {
        private Long reportId;
    }

}
