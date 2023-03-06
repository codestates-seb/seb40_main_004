package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.ArticleService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleDto.ResponseSimpleArticle uploadArticle(@Valid @RequestBody ArticleDto.RequestUploadArticle requestUploadArticle,
                                                          @RequestUser UserDto.UserInfo userInfo) {

        Article article = articleMapper.requestUploadArticleToEntity(requestUploadArticle);
        return articleService.upload(article, userInfo);
    }

    @PatchMapping("/{article-id}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto.ResponseSimpleArticle updateArticle(@Valid @RequestBody ArticleDto.RequestUpdateArticle requestUpdateArticle,
                                                          @RequestUser UserDto.UserInfo userInfo,
                                                          @PathVariable("article-id") Long articleId) {
        Article article = articleMapper.requestUpdateArticleToEntity(requestUpdateArticle, articleId);
        return articleService.update(article, userInfo);
    }

    @DeleteMapping("/{article-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArticle(@PathVariable("article-id") Long articleId,
                              @RequestUser UserDto.UserInfo userInfo) {
        articleService.deleteArticle(articleId, userInfo);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseMultiplePaging<ArticleDto.ResponseListTypeArticle> searchArticle(@Param("category") String category,
                                                                                    @Param("keyword") String keyword,
                                                                                    @Param("target") String target,
                                                                                    @Param("sort") String sort,
                                                                                    @RequestParam("page") Integer page,
                                                                                    @RequestParam("size") Integer size) {
        ResponseMultiplePaging<ArticleDto.ResponseListTypeArticle> responseMultiplePaging =
                articleService.searchArticleAsPaging(category, keyword, target, sort, page, size);
        return responseMultiplePaging;
    }

    @GetMapping("/{article-id}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto.ResponseDetailArticle findDetailArticle(@RequestUser UserDto.UserInfo userInfo,
                                                              @PathVariable("article-id") Long articleId,
                                                              HttpServletRequest request) {
        String ip = articleMapper.getIp(request);
        return articleService.findDetailArticle(articleId, userInfo, ip);
    }

    @PostMapping("/{article-id}/likes")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto.ResponseArticleLike pressLikeButton(@RequestUser UserDto.UserInfo userInfo,
                                                          @PathVariable("article-id") Long articleId) {
        return articleService.pressLikeButton(articleId, userInfo);
    }

    @PostMapping("/{article-id}/reports")
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleDto.ResponseReportArticle reportArticle(@PathVariable("article-id") Long articleId,
                                                          @RequestBody ArticleDto.RequestReportArticle reportArticle,
                                                          @RequestUser UserDto.UserInfo userInfo) {
        Report report = articleMapper.requestReportArticleToReport(reportArticle);

        return articleService.reportArticle(articleId, userInfo, report);
    }

}
