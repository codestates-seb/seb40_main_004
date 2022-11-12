package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface ArticleMapper {

    @Mapping(target = "category", ignore = true)
//    default Article requestUploadArticleToEntity(ArticleDto.RequestUploadArticle requestUploadArticle) {
//        return Article.builder().title(requestUploadArticle.getTitle()).content(requestUploadArticle.getContent())
//                .category(Category.builder().categoryName(requestUploadArticle.getCategory()).build())
//                .files(requestUploadArticle.getFileId().stream()
//                        .map(fileId -> File.builder().id(fileId).build()).collect(Collectors.toList()))
//                .thumbnail(requestUploadArticle.getThumbnail())
//                .articleTags(requestUploadArticle.getTags().stream()
//                        .map(tag -> ArticleTag.builder().tag(Tag.builder().id(tag).build()).build())
//                        .collect(Collectors.toList())).build();
//    }
        default  Article requestUploadArticleToEntity(ArticleDto.RequestUploadArticle requestUploadArticle) {
        return Article.builder().title(requestUploadArticle.getTitle())
                .content(requestUploadArticle.getContent())
                .thumbnail(requestUploadArticle.getThumbnail())
                .build();

    }

    @Mapping(source = "userInfo.id", target = "id")
    Article requestUpdateArticleToEntity(ArticleDto.RequestUpdateArticle updateArticle, UserDto.UserInfo userInfo);




    ArticleDto.ResponseSimpleArticle articleToResponseSimpleArticle(Long articleId);
}

