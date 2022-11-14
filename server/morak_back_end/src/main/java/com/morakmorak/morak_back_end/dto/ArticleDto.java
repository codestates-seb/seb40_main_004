package com.morakmorak.morak_back_end.dto;

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
        @Size(min = 15)
        private String title;
        @Size(min = 5)
        private String content;
        private List<TagDto.SimpleTag> tags = new ArrayList<>();
        private String category;
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

    public static class ResponseListTypeArticle {
        private Long articleId;
        private String category;
        private String title;
        private Integer clicks;
        private Integer likes;
        private Boolean isClosed;

        private List<TagDto.SimpleTag> tags;

        private Integer commentCount;
        private Integer answerCount;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;

        private UserDto.UserInfo userInfo;

    }

}
