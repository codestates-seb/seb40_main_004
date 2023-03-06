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
import com.morakmorak.morak_back_end.repository.article.ArticleTagRepository;
import com.morakmorak.morak_back_end.repository.notification.NotificationRepository;
import com.morakmorak.morak_back_end.repository.redis.RedisRepository;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserService userService;
    private final ArticleLikeRepository articleLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CommentMapper commentMapper;
    private final TagMapper tagMapper;
    private final PointCalculator pointCalculator;
    private final NotificationRepository notificationRepository;
    private final ReportRepository reportRepository;
    private final FileService fileService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final RedisRepository<ArticleDto.Ip> redisRepository;
    private final ArticleTagRepository articleTagRepository;

    public ArticleDto.ResponseSimpleArticle upload(Article article, UserDto.UserInfo userInfo) {
        User dbUser = userService.findVerifiedUserById(userInfo.getId());
        Category dbCategory = categoryService.findVerifiedCategoryByName(article.getCategory().getName());

        Article reBuildArticle = Article.builder()
                .title(article.getTitle())
                .content(article.getContent())
                .category(dbCategory)
                .user(dbUser)
                .thumbnail(article.getThumbnail())
                .build();
        int[][] a = new int[4][3];
        a[0] = new int[]{1, 3, 4, 5};
        dbCategory.getArticleList().add(reBuildArticle);
        dbUser.getArticles().add(reBuildArticle);

        bridgeFileToArticle(article, reBuildArticle);
        bridgeTagToArticle(article, reBuildArticle);

        Article dbArticle = articleRepository.save(reBuildArticle);

        dbUser.plusPoint(dbArticle, pointCalculator);

        return articleMapper.articleToResponseSimpleArticle(dbArticle.getId());
    }

    public ArticleDto.ResponseSimpleArticle update(Article article, UserDto.UserInfo userInfo) {

        Article dbArticle = findArticleRelationWithUser(article.getId());
        checkArticleStatus(dbArticle);
        checkArticlePerMission(dbArticle, userInfo);
        dbArticle.changeArticle(article);

        deleteOriginTagInArticle(dbArticle);
        bridgeFileToArticle(article, dbArticle);
        bridgeTagToArticle(article,dbArticle);

        return articleMapper.articleToResponseSimpleArticle(dbArticle.getId());
    }

    private void deleteOriginTagInArticle(Article dbArticle) {
        dbArticle.getArticleTags().stream()
                .forEach(articleTag -> {
                    articleTag.removeTo(dbArticle, articleTag.getTag());
                    articleTagRepository.delete(articleTag);
                });
    }

    private void bridgeFileToArticle(Article article, Article reBuildArticle) {
        article.getFiles().stream().forEach(file -> {
            File dbFile = fileService.findVerifiedFileById(file.getId());
            dbFile.injectTo(reBuildArticle);
        });
    }

    private void bridgeTagToArticle(Article article, Article reBuildArticle) {
        article.getArticleTags().stream()
                .forEach(articleTag -> {
                    Tag dbTag = tagService.findVerifiedTagByTagName(articleTag.getTag().getName());
                    ArticleTag reBuildArticleTag = ArticleTag.builder().article(reBuildArticle).tag(dbTag).build();
                    articleTagRepository.save(reBuildArticleTag);
                });
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

    public Article findVerifiedArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    private Article findArticleRelationWithUser(Long articleId) {
        return articleRepository.findArticleRelationWithUser(articleId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.ARTICLE_NOT_FOUND));
    }
    private void checkArticlePerMission(Article article, UserDto.UserInfo userInfo) {
        if (!article.getUser().getId().equals(userInfo.getId())) {
            throw new BusinessLogicException(ErrorCode.INVALID_USER);
        }
    }

    public ResponseMultiplePaging<ArticleDto.ResponseListTypeArticle> searchArticleAsPaging(
            String category, String keyword, String target, String sort, Integer page, Integer size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<Article> articles = articleRepository.search(category, keyword, target, sort, pageRequest);

        List<ArticleDto.ResponseListTypeArticle> mapper = getResponseListTypeArticles(articles);

        return new ResponseMultiplePaging<>(mapper, articles);
    }

    private List<ArticleDto.ResponseListTypeArticle> getResponseListTypeArticles(Page<Article> articles) {
        return articles.getContent().stream().map(article -> {

            Integer likes = article.getArticleLikes().size();
            Integer commentCount = article.getComments().size();
            Integer answerCount = article.getAnswers().size();

            List<TagDto.SimpleTag> tags = article.getArticleTags().stream()
                    .map(articleTag -> tagMapper.tagEntityToTagDto(articleTag.getTag()))
                    .collect(Collectors.toList());

            return articleMapper.articleToResponseSearchResultArticle(article, commentCount, answerCount, tags, likes);
        }).collect(Collectors.toList());
    }

    public ArticleDto.ResponseDetailArticle findDetailArticle(Long articleId, UserDto.UserInfo userInfo, String ip) {
        Article dbArticle = findVerifiedArticle(articleId);
        checkArticleStatus(dbArticle);
        dbArticle.plusClicks();
//        checkExistClickIpOrElsePlus(dbArticle.getId(), ip, dbArticle);

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
            String report = "이 글은 신고가 누적되어 더이상 확인하실 수 없습니다.";
            return articleMapper.articleToResponseBlockedArticle(dbArticle, isLiked, isBookmarked,
                    report,new ArrayList<>(),new ArrayList<>(),likes);
        }

        ArticleDto.ResponseDetailArticle responseDetailArticle =
                articleMapper.articleToResponseDetailArticle(dbArticle, isLiked, isBookmarked,
                        tags, comments, likes);

        return responseDetailArticle;
    }

//    private void checkExistClickIpOrElsePlus(Long articleId, String ip, Article dbArticle) {
//
//        if (redisRepository.getData(ip, ArticleDto.Ip.class).isPresent()) {
//            ArticleDto.Ip savedIp = redisRepository.getData(ip, ArticleDto.Ip.class).get();
//            boolean contains = savedIp
//                    .getArticleId().contains(articleId);
//            if (!contains) {
//                dbArticle.plusClicks();
//                savedIp.getArticleId().add(articleId);
//                redisRepository.saveData(ip, savedIp, (long) 24 * 36 * 100000);
//            }
//        }
//        else if (redisRepository.getData(ip, ArticleDto.Ip.class).isEmpty()) {
//            Set<Long> articleIds = new HashSet<>();
//            articleIds.add(articleId);
//            ArticleDto.Ip build = ArticleDto.Ip.builder()
//                    .ip(ip)
//                    .articleId(articleIds)
//                    .build();
//            redisRepository.saveData(ip, build, (long) 24 * 36 * 100000);
//            dbArticle.plusClicks();
//        }
//    }

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

                    dbUser.plusPoint(articleLike, pointCalculator);
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
        Set<Long> collect = dbArticle.getReports().stream()
                .map(report -> report.getUser().getId())
                .collect(Collectors.toSet());

        if (collect.contains(dbUser.getId())) {
            throw new BusinessLogicException((ErrorCode.USER_EXISTS));
        }

        reportArticle.injectTo(dbUser, dbArticle);
        dbArticle.getReports().add(reportArticle);
        dbUser.getReports().add(reportArticle);

        Report dbReport = reportRepository.save(reportArticle);

       return articleMapper.reportToResponseArticle(dbReport);
    }
}
