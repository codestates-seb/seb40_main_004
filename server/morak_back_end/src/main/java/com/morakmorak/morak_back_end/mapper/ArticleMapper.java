package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.ArticleDto;

import com.morakmorak.morak_back_end.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface ArticleMapper {

    @Mapping(target = "category", ignore = true)
        default  Article requestUploadArticleToEntity(ArticleDto.RequestUploadArticle requestUploadArticle) {
        return Article.builder().title(requestUploadArticle.getTitle())
                .content(requestUploadArticle.getContent())
                .thumbnail(requestUploadArticle.getThumbnail())
                .build();

    }

    @Mapping(source = "articleId", target = "id")
    Article requestUpdateArticleToEntity(ArticleDto.RequestUpdateArticle updateArticle, Long articleId);




    ArticleDto.ResponseSimpleArticle articleToResponseSimpleArticle(Long articleId);
}

