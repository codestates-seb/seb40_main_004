package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.ArticleDto;

import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.TagDto;
import com.morakmorak.morak_back_end.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;


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



    @Mapping(source = "article.id", target = "articleId")
    @Mapping(source = "article.category.name", target = "category")
    @Mapping(source = "article.user.id", target = "userInfo.userId")
    @Mapping(source = "article.user.nickname", target = "userInfo.nickname")
    @Mapping(source = "article.user.grade", target = "userInfo.grade")
    @Mapping(source = "article.user.avatar.id", target = "avatar.avatarId")
    @Mapping(source = "article.user.avatar.originalFilename", target = "avatar.filename")
    @Mapping(source = "article.user.avatar.remotePath", target = "avatar.remotePath")
    ArticleDto.ResponseListTypeArticle articleToResponseSearchResultArticle(Article article,
                                                                            Integer commentCount,
                                                                            Integer answerCount,
                                                                            List<TagDto.SimpleTag> tags,
                                                                            Integer likes);

    @Mapping(source = "article.id", target = "articleId")
    @Mapping(source = "article.category.name", target = "category")
    @Mapping(source = "article.user.id", target = "userInfo.userId")
    @Mapping(source = "article.user.nickname", target = "userInfo.nickname")
    @Mapping(source = "article.user.grade", target = "userInfo.grade")
    @Mapping(source = "article.user.avatar.id", target = "avatar.avatarId")
    @Mapping(source = "article.user.avatar.originalFilename", target = "avatar.filename")
    @Mapping(source = "article.user.avatar.remotePath", target = "avatar.remotePath")
    @Mapping(source = "comments", target = "comments")
    ArticleDto.ResponseDetailArticle articleToResponseDetailArticle(Article article,
                                                                    Boolean isLiked,
                                                                    Boolean isBookmarked,
                                                                    List<TagDto.SimpleTag> tags,
                                                                    List<CommentDto.Response> comments,
                                                                    Integer likes
    );

}
