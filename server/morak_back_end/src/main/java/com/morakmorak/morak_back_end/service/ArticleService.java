package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
            Article article, UserDto.UserInfo userInfo, List<TagDto.SimpleTag> tags,
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

    public ArticleDto.ResponseSimpleArticle update(Article article, UserDto.UserInfo userInfo, List<TagDto.SimpleTag> tags,
                                                   List<FileDto.RequestFileWithId> files) {
        Article dbArticle = findVerifiedArticle(article.getId());

        dbArticle.updateArticleElement(article);

        checkArticlePerMission(dbArticle, userInfo);

        fusionFileDtoWithArticle(dbArticle, files);

        fusionTagDtoWithArticle(dbArticle, tags);

        return articleMapper.articleToResponseSimpleArticle(dbArticle.getId());
    }

    public Boolean deleteArticle(Long articleId, UserDto.UserInfo userInfo) {
        Article dbArticle = findVerifiedArticle(articleId);
        checkArticlePerMission(dbArticle, userInfo);
        dbArticle.changeArticleStatus(ArticleStatus.REMOVED);
        return true;
    }

    public Article findVerifiedArticle(Long articleId) {
        return articleRepository.findById(articleId).orElseThrow(() -> new BusinessLogicException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    public Boolean checkArticlePerMission(Article article, UserDto.UserInfo userInfo) {
        if (!article.getUser().getId().equals(userInfo.getId())) {
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
        Category dbCategory = categoryRepository.findCategoryByName(category.getName())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.CATEGORY_NOT_FOUND));
        article.infectCategoryForMapping(dbCategory);

        return true;
    }

    public Boolean fusionTagDtoWithArticle(Article article, List<TagDto.SimpleTag> tags) {
        for (int i = article.getArticleTags().size() - 1; i >= 0; i--) {
            article.getArticleTags().remove(i);
        }
        tags.stream().forEach(tag -> {
            Tag dbTag = tagRepository.findById(tag.getTagId())
                    .orElseThrow(() -> new BusinessLogicException(ErrorCode.TAG_NOT_FOUND));
            ArticleTag newArticleTag = ArticleTag.builder().article(article).tag(dbTag).build();
            article.getArticleTags().add(newArticleTag);
            dbTag.getArticleTags().add(newArticleTag);
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

    public ResponseMultiplePaging<ArticleDto.ResponseListTypeArticle> searchArticleAsPaging(String category, String keyword, String target, String sort, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);

        Page<Article> articles;

        if (target.equals("tag")) {
             articles = articleRepository.tagSearch(category, keyword, target, sort, pageRequest);

        } else {
            articles = articleRepository.search(category, keyword, target, sort, pageRequest);
        }

        List<ArticleDto.ResponseListTypeArticle> mapper = articles.getContent().stream().map(article -> {
            Integer likes = article.getArticleLikes().size();
            Integer commentCount = article.getComments().size();
            Integer answerCount = article.getAnswers().size();
            List<Tag> tags = article.getArticleTags().stream()
                    .map(articleTag -> articleTag.getTag()).collect(Collectors.toList());
            return articleMapper.articleToResponseSearchResultArticle(article, commentCount, answerCount, tags, likes);
        }).collect(Collectors.toList());

        return new ResponseMultiplePaging<>(mapper, articles);
    }
}
