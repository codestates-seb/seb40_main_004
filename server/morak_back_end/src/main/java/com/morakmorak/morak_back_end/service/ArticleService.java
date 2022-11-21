package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.mapper.CommentMapper;
import com.morakmorak.morak_back_end.mapper.TagMapper;
import com.morakmorak.morak_back_end.repository.*;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final ArticleLikeRepository articleLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CommentMapper commentMapper;
    private final TagMapper tagMapper;

    public ArticleDto.ResponseSimpleArticle upload(
            Article article, UserDto.UserInfo userInfo, List<TagDto.SimpleTag> tags,
            List<FileDto.RequestFileWithId> files, Category category
    ) {
        article.injectUserForMapping(userService.findVerifiedUserById(userInfo.getId()));

        findDbFilesAndInjectWithArticle(article, files);

        findDbTagsAndInjectWithArticle(article, tags);

        findDbCategoryAndInjectWithArticle(article, category);

        Article dbArticle = articleRepository.save(article);

        return articleMapper.articleToResponseSimpleArticle(dbArticle.getId());
    }

    public ArticleDto.ResponseSimpleArticle update(Article article, UserDto.UserInfo userInfo, List<TagDto.SimpleTag> tags,
                                                   List<FileDto.RequestFileWithId> files) {
        Article dbArticle = findVerifiedArticle(article.getId());

        dbArticle.updateArticleElement(article);

        checkArticlePerMission(dbArticle, userInfo);

        findDbFilesAndInjectWithArticle(dbArticle, files);

        findDbTagsAndInjectWithArticle(dbArticle, tags);

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

    public Boolean findDbCategoryAndInjectWithArticle(Article article, Category category) {
        Category dbCategory = categoryRepository.findCategoryByName(category.getName())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.CATEGORY_NOT_FOUND));
        article.injectCategoryForMapping(dbCategory);

        return true;
    }

    public Boolean findDbTagsAndInjectWithArticle(Article article, List<TagDto.SimpleTag> tags) {
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

    public Boolean findDbFilesAndInjectWithArticle(Article article, List<FileDto.RequestFileWithId> files) {
        files.stream()
                .forEach(file -> {
                    File dbFile = fileRepository.findById(file.getFileId())
                            .orElseThrow(() -> new BusinessLogicException(ErrorCode.FILE_NOT_FOUND));
                    dbFile.injectArticleForFile(article);

                });
        return true;
    }

    public ResponseMultiplePaging<ArticleDto.ResponseListTypeArticle> searchArticleAsPaging(String category, String keyword, String target, String sort, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<Article> articles = articleRepository.search(category, keyword, target, sort, pageRequest);

        List<ArticleDto.ResponseListTypeArticle> mapper = articles.getContent().stream().map(article -> {
            Integer likes = article.getArticleLikes().size();
            Integer commentCount = article.getComments().size();
            Integer answerCount = article.getAnswers().size();
            List<TagDto.SimpleTag> tags = article.getArticleTags().stream()
                    .map(articleTag -> tagMapper.tagEntityToTagDto(articleTag.getTag())).collect(Collectors.toList());
            return articleMapper.articleToResponseSearchResultArticle(article, commentCount, answerCount, tags, likes);
        }).collect(Collectors.toList());

        return new ResponseMultiplePaging<>(mapper, articles);
    }

    public ArticleDto.ResponseDetailArticle findDetailArticle(Long articleId, UserDto.UserInfo userInfo) {
        Article dbArticle = findVerifiedArticle(articleId);
        Boolean isLiked = Boolean.FALSE;
        Boolean isBookmarked = Boolean.FALSE;

        if (userInfo != null) {
            Long userId = userInfo.getId();
            isLiked = articleLikeRepository.checkUserLiked(userId, dbArticle.getId()).isPresent();
            isBookmarked = bookmarkRepository.checkUserBookmarked(userId, dbArticle.getId()).isPresent();
        }

        Integer likes = dbArticle.getArticleLikes().size();

        List<TagDto.SimpleTag> tags = dbArticle.getArticleTags().stream().map(articleTag -> {
            TagDto.SimpleTag simpleTag = tagMapper.tagEntityToTagDto(articleTag.getTag());
            return simpleTag;
        }).collect(Collectors.toList());

        List<CommentDto.Response> comments = dbArticle.getComments().stream()
                .map(commentMapper::commentToCommentDto).collect(Collectors.toList());

        ArticleDto.ResponseDetailArticle responseDetailArticle =
                articleMapper.articleToResponseDetailArticle(dbArticle, isLiked, isBookmarked,
                        tags, comments, likes);

        return responseDetailArticle;
    }

    public ArticleDto.ResponseArticleLike pressLikeButton(Long articleId, UserDto.UserInfo userInfo) {

        Optional.ofNullable(userInfo).orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        Article dbArticle = findVerifiedArticle(articleId);

        User dbUser = userService.findVerifiedUserById(userInfo.getId());

        articleLikeRepository.checkUserLiked(dbUser.getId(), dbArticle.getId()).ifPresentOrElse(
                articleLike -> {
                    articleLikeRepository.deleteById(articleLike.getId());
                },
                () -> {
                    ArticleLike articleLike = ArticleLike.builder().article(dbArticle).user(dbUser).build();
                    dbArticle.getArticleLikes().add(articleLike);
                    dbUser.getArticleLikes().add(articleLike);

                }
        );

        Boolean isLiked = articleLikeRepository
                .checkUserLiked(dbUser.getId(), dbArticle.getId()).isPresent();

        Integer likeCount = dbArticle.getArticleLikes().size();

        return articleMapper.makingResponseArticleLikeDto(dbArticle.getId(), dbUser.getId(), isLiked, likeCount);
    }

    public ArticleDto.ResponseReportArticle reportArticle(Long articleId, UserDto.UserInfo userInfo, Report reportArticle) {
        Article dbArticle = findVerifiedArticle(articleId);
        
        User dbUser = null;

        if (userInfo != null) {
            dbUser = userService.findVerifiedUserById(userInfo.getId());
        } else {
            throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
        }
        
        reportArticle.injectMappingUserAndArticle(dbUser, dbArticle);
        dbArticle.getReports().add(reportArticle);
        dbUser.getReports().add(reportArticle);

        Report dbReport = reportRepository.save(reportArticle);

       return articleMapper.reportToResponseArticle(dbReport);
    }
}
