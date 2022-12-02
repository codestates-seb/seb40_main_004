package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.domain.NotificationGenerator;
import com.morakmorak.morak_back_end.domain.PointCalculator;
import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.mapper.CommentMapper;
import com.morakmorak.morak_back_end.mapper.TagMapper;
import com.morakmorak.morak_back_end.repository.*;
import com.morakmorak.morak_back_end.repository.article.ArticleLikeRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.notification.NotificationRepository;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.*;
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
    private final PointCalculator pointCalculator;
    private final NotificationRepository notificationRepository;
    private final ReportRepository reportRepository;

    public ArticleDto.ResponseSimpleArticle upload(
            Article article, UserDto.UserInfo userInfo, List<TagDto.SimpleTag> tags,
            List<FileDto.RequestFileWithId> files, Category category
    ) {
        User dbUser = userService.findVerifiedUserById(userInfo.getId());

        article.injectUserForMapping(dbUser);

        findDbFilesAndInjectWithArticle(article, files);

        findDbTagsAndInjectWithArticle(article, tags);

        findDbCategoryAndInjectWithArticle(article, category);

        Article dbArticle = articleRepository.save(article);

        dbUser.addPoint(dbArticle, pointCalculator);

        return articleMapper.articleToResponseSimpleArticle(dbArticle.getId());
    }

    public ArticleDto.ResponseSimpleArticle update(Article article, UserDto.UserInfo userInfo, List<TagDto.SimpleTag> tags,
                                                   List<FileDto.RequestFileWithId> files) {
        Article dbArticle = findVerifiedArticle(article.getId());

        checkArticleStatus(dbArticle);

        dbArticle.updateArticleElement(article);

        checkArticlePerMission(dbArticle, userInfo);

        findDbFilesAndInjectWithArticle(dbArticle, files);

        findDbTagsAndInjectWithArticle(dbArticle, tags);

        return articleMapper.articleToResponseSimpleArticle(dbArticle.getId());
    }

    public Boolean deleteArticle(Long articleId, UserDto.UserInfo userInfo) {

        Article dbArticle = findVerifiedArticle(articleId);

        checkArticleStatus(dbArticle);

        checkArticlePerMission(dbArticle, userInfo);

        dbArticle.changeArticleStatus(ArticleStatus.REMOVED);

        User user = dbArticle.getUser();
        user.minusPoint(dbArticle, pointCalculator);
        return true;
    }

    @Transactional(readOnly = true)
    public Article findVerifiedArticle(Long articleId) {
        return articleRepository.findById(articleId).orElseThrow(() -> new BusinessLogicException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public void checkArticlePerMission(Article article, UserDto.UserInfo userInfo) {
        if (!article.getUser().getId().equals(userInfo.getId())) {
            throw new BusinessLogicException(ErrorCode.INVALID_USER);
        }
    }

    public void findDbCategoryAndInjectWithArticle(Article article, Category category) {
        Category dbCategory = categoryRepository.findCategoryByName(category.getName())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.CATEGORY_NOT_FOUND));
        article.injectCategoryForMapping(dbCategory);
    }

    public void findDbTagsAndInjectWithArticle(Article article, List<TagDto.SimpleTag> tags) {
        for (int i = article.getArticleTags().size() - 1; i >= 0; i--) {
            article.getArticleTags().remove(i);
        }
        tags.stream().forEach(tag -> {
            Tag dbTag = tagRepository.findTagByName(tag.getName())
                    .orElseThrow(() -> new BusinessLogicException(ErrorCode.TAG_NOT_FOUND));
            ArticleTag newArticleTag = ArticleTag.builder().article(article).tag(dbTag).build();
            article.getArticleTags().add(newArticleTag);
            dbTag.getArticleTags().add(newArticleTag);
        });
    }

    public void findDbFilesAndInjectWithArticle(Article article, List<FileDto.RequestFileWithId> files) {
        files.stream()
                .forEach(file -> {
                    File dbFile = fileRepository.findById(file.getFileId())
                            .orElseThrow(() -> new BusinessLogicException(ErrorCode.FILE_NOT_FOUND));
                    dbFile.injectArticleForFile(article);
                });
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
        Article article = findVerifiedArticle(articleId);
        checkArticleStatus(article);
        Article dbArticle = article.addClicks();

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

        if (dbArticle.getReports().size() >= 5) {
            String report = "이 글은 신고가 누적되 더이상 확인하실 수 없습니다.";
            
            return articleMapper.articleToResponseBlockedArticle(dbArticle, isLiked, isBookmarked,
                    report,new ArrayList<>(),new ArrayList<>(),likes);
        }

        ArticleDto.ResponseDetailArticle responseDetailArticle =
                articleMapper.articleToResponseDetailArticle(dbArticle, isLiked, isBookmarked,
                        tags, comments, likes);

        return responseDetailArticle;
    }

    private Article checkArticleStatus(Article verifiedArticle) {
        if (!verifiedArticle.statusIsPosting()) {
            throw new BusinessLogicException(ErrorCode.NO_ACCESS_TO_THAT_OBJECT);
        }
        return verifiedArticle;
    }

    public ArticleDto.ResponseArticleLike pressLikeButton(Long articleId, UserDto.UserInfo userInfo) {

        Optional.ofNullable(userInfo).orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        Article dbArticle = findVerifiedArticle(articleId);

        checkArticleStatus(dbArticle);

        User dbUser = userService.findVerifiedUserById(userInfo.getId());

        articleLikeRepository.checkUserLiked(dbUser.getId(), dbArticle.getId()).ifPresentOrElse(
                articleLike -> {
                    dbUser.minusPoint(articleLike, pointCalculator);
                    articleLikeRepository.deleteById(articleLike.getId());
                },
                () -> {
                    ArticleLike articleLike = ArticleLike.builder()
                            .article(dbArticle)
                            .user(dbUser)
                            .build();

                    dbArticle.getArticleLikes().add(articleLike);
                    dbUser.getArticleLikes().add(articleLike);

                    if (dbArticle.getArticleLikes().size() % 10 == 0) {
                        NotificationGenerator generator = NotificationGenerator.of(articleLike, dbArticle.getArticleLikes().size());
                        Notification notification = generator.generateNotification();
                        notificationRepository.save(notification);
                    }

                    dbUser.addPoint(articleLike, pointCalculator);
                }
        );

        Boolean isLiked = articleLikeRepository
                .checkUserLiked(dbUser.getId(), dbArticle.getId()).isPresent();

        Integer likeCount = dbArticle.getArticleLikes().size();

        return articleMapper.makingResponseArticleLikeDto(dbArticle.getId(), dbUser.getId(), isLiked, likeCount);
    }

    public ArticleDto.ResponseReportArticle reportArticle(Long articleId, UserDto.UserInfo userInfo, Report reportArticle) {

        Article dbArticle = findVerifiedArticle(articleId);
        checkArticleStatus(dbArticle);

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
