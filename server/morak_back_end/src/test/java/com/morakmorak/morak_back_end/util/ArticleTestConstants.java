package com.morakmorak.morak_back_end.util;

import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.dto.FileDto;
import com.morakmorak.morak_back_end.dto.TagDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.TagName;

import java.util.List;

public class ArticleTestConstants {
    public final static List<TagDto.RequestTagWithIdAndName> REQUEST_TAG_WITH_ID_AND_NAMES = List.of(TagDto.RequestTagWithIdAndName.builder()
            .tagId(1L).tagName("Java").build());
    public final static List<FileDto.RequestFileWithId> REQUEST_FILE_WITH_IDS = List.of(FileDto.RequestFileWithId.builder()
            .FileId(1L).build(), FileDto.RequestFileWithId.builder()
            .FileId(2L).build());

    public final static Category REQUEST_STRING_CATEGORY = Category.builder()
            .categoryName("INFO").build();
    public final static ArticleDto.RequestUploadArticle REQUEST_UPLOAD_ARTICLE
            = ArticleDto.RequestUploadArticle.builder()
            .title("안녕하세요 타이틀입니다. 잘 부탁드립니다. 타이틀은 신경씁니다.").content("콘텐트입니다. 잘부탁드립니다.")
            .tags(REQUEST_TAG_WITH_ID_AND_NAMES)
            .fileId(REQUEST_FILE_WITH_IDS)
            .category(REQUEST_STRING_CATEGORY.getCategoryName())
            .thumbnail(1L)
            .build();
    public final static ArticleDto.RequestUpdateArticle REQUEST_UPDATE_ARTICLE =
            ArticleDto.RequestUpdateArticle.builder()
                    .title("안녕하세요 타이틀입니다. 잘 부탁드립니다. 타이틀은 신경씁니다.").content("콘텐트입니다. 잘부탁드립니다.")
                    .tags(REQUEST_TAG_WITH_ID_AND_NAMES)
                    .fileId(REQUEST_FILE_WITH_IDS)
                    .thumbnail(1L)
                    .build();
    public final static Article ARTICLE =
            Article.builder().title("안녕하세요 타이틀입니다. 잘 부탁드립니다. 타이틀은 신경씁니다.")
                    .content("콘텐트입니다. 잘부탁드립니다.")
                    .thumbnail(1L)
                    .files(List.of(File.builder().id(1L).article(Article.builder().id(1L).build()).build(),
                            File.builder().id(2L).article(Article.builder().id(1L).build()).build()))
                    .articleTags(List.of(ArticleTag.builder().tag(Tag.builder().tagName(TagName.JAVA).build()).build()))
                    .category(Category.builder().categoryName("Info").build())
                    .user(User.builder().id(1L).build())
                    .files(List.of(File.builder().article(Article.builder().id(1L).build()).build()))
                    .build();

    public final static ArticleDto.ResponseSimpleArticle RESPONSE_SIMPLE_ARTICLE =
            ArticleDto.ResponseSimpleArticle.builder().articleId(1L).build();
}
