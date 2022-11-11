package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.repository.ArticleRepository;
import com.morakmorak.morak_back_end.repository.CategoryRepository;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final FileRepository fileRepository;
    private final TagRepository tagRepository;
    private final UserService userService;

    private final CategoryRepository categoryRepository;

    public ArticleDto.ResponseSimpleArticle upload(
            Article article, UserDto.UserInfo userInfo, List<TagDto.RequestTagWithIdAndName> tags,
            List<FileDto.RequestFileWithId> files, Category category
    ) {
        article.injectUserForMapping(userService.findVerifiedUserById(userInfo.getId()));

        fusionFileDtoWithArticle(article, files);

        fusionTagDtoWithArticle(article, tags);

        fusionCategoryWIthArticle(article, category);

        fusionVoteWithArticle(article);

        Article dbArticle = articleRepository.save(article);

        return articleMapper.articleToResponseSimpleArticle(dbArticle.getId());
    }

    public ArticleDto.ResponseSimpleArticle update(Article article, UserDto.UserInfo userInfo, List<TagDto.RequestTagWithIdAndName> tags,
                                                   List<FileDto.RequestFileWithId> files) {

        checkArticlePerMission(article, userInfo);

        Article dbArticle = findVerifiedArticle(article.getId());
        dbArticle.updateArticleElement(article);

        fusionFileDtoWithArticle(dbArticle, files);

        fusionTagDtoWithArticle(dbArticle, tags);

        return articleMapper.articleToResponseSimpleArticle(dbArticle.getId());
    }

    public Boolean deleteArticle(Long articleId, UserDto.UserInfo userInfo) {
        Article dbArticle = findVerifiedArticle(articleId);
        checkArticlePerMission(dbArticle,userInfo);
        dbArticle.changeArticleStatus(ArticleStatus.REMOVED);
        return true;
    }

    public Article findVerifiedArticle(Long articleId) {
        return articleRepository.findById(articleId).orElseThrow(() -> new BusinessLogicException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    public Boolean checkArticlePerMission(Article article, UserDto.UserInfo userInfo) {
        if (article.getUser().getId() != userInfo.getId()) {
            throw new BusinessLogicException(ErrorCode.INVALID_USER);
        }
        return true;
    }

    public Boolean fusionVoteWithArticle(Article article) {
        Vote vote = Vote.builder().article(article).user(article.getUser()).build();
        article.infectVoteForMapping(vote);

        return true;
    }

    public Boolean fusionCategoryWIthArticle(Article article, Category category) {
        Category dbCategory = categoryRepository.findByCategoryByName(category.getCategoryName())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.CATEGORY_NOT_FOUND));
        article.infectCategoryForMapping(dbCategory);

        return true;
    }

    public Boolean fusionTagDtoWithArticle(Article article, List<TagDto.RequestTagWithIdAndName> tags) {
         tags.stream().forEach(tag -> {
            Tag dbTag = tagRepository.findById(tag.getTagId())
                    .orElseThrow(() -> new BusinessLogicException(ErrorCode.TAG_NOT_FOUND));
            ArticleTag.builder().build().injectMappingForArticleAndTag(article, dbTag);

        });
        return true;
    }

    public Boolean fusionFileDtoWithArticle(Article article, List<FileDto.RequestFileWithId> files) {
        files.stream()
                .forEach(file -> {
                    File dbFile = fileRepository.findById(file.getFileId())
                            .orElseThrow(() -> new BusinessLogicException(ErrorCode.FILE_NOT_FOUND));
                    dbFile.injectArticleForFile(article);

                });
        return true;
    }
}
