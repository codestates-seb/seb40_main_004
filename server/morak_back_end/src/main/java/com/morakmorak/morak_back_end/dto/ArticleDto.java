package com.morakmorak.morak_back_end.dto;

import com.morakmorak.morak_back_end.entity.Category;
import lombok.*;
import net.bytebuddy.asm.Advice;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.File;
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
        private List<TagDto.RequestTagWithIdAndName> tags = new ArrayList<>();
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
        private List<TagDto.RequestTagWithIdAndName> tags = new ArrayList<>();
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
}
