package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.FileDto;
import com.morakmorak.morak_back_end.dto.TagDto;
import com.morakmorak.morak_back_end.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface ArticleMapper {

    @Mapping(target = "category", ignore = true)
    default  Article requestUploadArticleToEntity(ArticleDto.RequestUploadArticle requestUploadArticle) {
        return Article.builder()
                .title(requestUploadArticle.getTitle())
                .content(requestUploadArticle.getContent())
                .category(Category.builder().name(requestUploadArticle.getCategory()).build())
                .articleTags(getArticleTagsFromTagDto(requestUploadArticle.getTags()))
                .files(getFilesFromFileDto(requestUploadArticle.getFileId()))
                .thumbnail(requestUploadArticle.getThumbnail())
                .build();
    }


    @Mapping(source = "articleId", target = "id")
    Article requestUpdateArticleToEntity(ArticleDto.RequestUpdateArticle updateArticle, Long articleId);

//    default Article requestUpdateArticleToEntity(ArticleDto.RequestUpdateArticle updateArticle, Long articleId) {
//
//        return Article.builder()
//                .title(updateArticle.getTitle())
//                .content(updateArticle.getContent())
//                .articleTags(getArticleTagsFromTagDto(updateArticle.getTags()))
//                .files(getFilesFromFileDto(updateArticle.getFileId()))
//                .thumbnail(updateArticle.getThumbnail())
//                .build();
//    }

     private static List<ArticleTag> getArticleTagsFromTagDto(List<TagDto.SimpleTag> simpleTags) {
        List<ArticleTag> articleTags = simpleTags.stream()
                .map(simpleTag ->
                        ArticleTag.builder().
                                tag(Tag.builder()
                                        .id(simpleTag.getTagId())
                                        .name(simpleTag.getName())
                                        .build())
                                .build())
                .collect(Collectors.toList());
        return articleTags;
    }

     private static List<File> getFilesFromFileDto(List<FileDto.RequestFileWithId> requestFileWithId) {
        List<File> files = requestFileWithId.stream()
                .map(fileDto ->
                        File.builder()
                                .id(fileDto.getFileId())
                                .build())
                .collect(Collectors.toList());
        return files;
    }


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


    @Mapping(source = "report", target = "title")
    @Mapping(source = "report", target = "content")
    @Mapping(source = "article.id", target = "articleId")
    @Mapping(source = "article.category.name", target = "category")
    @Mapping(source = "article.user.id", target = "userInfo.userId")
    @Mapping(source = "article.user.nickname", target = "userInfo.nickname")
    @Mapping(source = "article.user.grade", target = "userInfo.grade")
    @Mapping(source = "article.user.avatar.id", target = "avatar.avatarId")
    @Mapping(source = "article.user.avatar.originalFilename", target = "avatar.filename")
    @Mapping(source = "article.user.avatar.remotePath", target = "avatar.remotePath")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "comments", target = "comments")
    ArticleDto.ResponseDetailArticle articleToResponseBlockedArticle(Article article,
                                                                     Boolean isLiked,
                                                                     Boolean isBookmarked,
                                                                     String report,
                                                                     List<TagDto.SimpleTag> tags,
                                                                     List<CommentDto.Response> comments,
                                                                     Integer likes

    );

    ArticleDto.ResponseArticleLike makingResponseArticleLikeDto(Long articleId, Long userId, Boolean isLiked, Integer likeCount);


    Report requestReportArticleToReport(ArticleDto.RequestReportArticle reportArticle);

    @Mapping(source = "report.id", target = "reportId")
    ArticleDto.ResponseReportArticle reportToResponseArticle(Report report);

}
