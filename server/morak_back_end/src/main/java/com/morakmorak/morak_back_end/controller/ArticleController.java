package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.mapper.CategoryMapper;
import com.morakmorak.morak_back_end.mapper.FileMapper;
import com.morakmorak.morak_back_end.mapper.TagMapper;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.ArticleService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;
    private final FileMapper fileMapper;
    private final CategoryMapper categoryMapper;


    @PostMapping
    public ResponseEntity uploadArticle(@Valid @RequestBody ArticleDto.RequestUploadArticle requestUploadArticle,
                                        @RequestUser UserDto.UserInfo userInfo) {
        log.info("UPLOAD_ARTICLE_INPUT_{}", requestUploadArticle);
        Article article = articleMapper.requestUploadArticleToEntity(requestUploadArticle);
        List<TagDto.SimpleTag> tags = tagMapper.requestTagWithIdAndNameToTagDto(requestUploadArticle);
        List<FileDto.RequestFileWithId> files = fileMapper.RequestFileWithIdToFile(requestUploadArticle);
        Category category = categoryMapper.RequestUploadArticleToCategory(requestUploadArticle);
        ArticleDto.ResponseSimpleArticle upload = articleService.upload(article, userInfo, tags, files, category);
        log.info("UPLOAD_ARTICLE_OUTPUT_{}", upload);
        return new ResponseEntity<>(upload, HttpStatus.CREATED);
    }

    @PatchMapping("/{article-id}")
    public ResponseEntity updateArticle(@Valid @RequestBody ArticleDto.RequestUpdateArticle requestUpdateArticle,
                                        @RequestUser UserDto.UserInfo userInfo,
                                        @PathVariable("article-id") Long articleId) {
        log.info("UPDATE_ARTICLE_INPUT_{}", requestUpdateArticle);
        log.info("UPDATE_ARTICLE_USERINFO_INPUT_{}", userInfo);

        Article article = articleMapper.requestUpdateArticleToEntity(requestUpdateArticle, articleId);
        List<TagDto.SimpleTag> tags = tagMapper.requestTagWithIdAndNameToTagDto(requestUpdateArticle);
        List<FileDto.RequestFileWithId> files = fileMapper.RequestFileWithIdToFile(requestUpdateArticle);
        ArticleDto.ResponseSimpleArticle update = articleService.update(article, userInfo, tags, files);

        log.info("UPDATE_ARTICLE_OUTPUT_{}", update);
        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    @DeleteMapping("/{article-id}")
    public ResponseEntity deleteArticle(@PathVariable("article-id") Long articleId,
                                        @RequestUser UserDto.UserInfo userInfo) {
        log.info("DELETE_ARTICLE_INPUT_{}", articleId);
        log.info("DELETE_ARTICLE_USERINFO_INPUT_{}", userInfo);
        articleService.deleteArticle(articleId, userInfo);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity searchArticle(@Param("category") String category,
                                        @Param("keyword") String keyword,
                                        @Param("target") String target,
                                        @Param("sort") String sort,
                                        @RequestParam("page") Integer page,
                                        @RequestParam("size") Integer size) {
        log.info("SEARCHARTICLE_INPUT_PARAM_{}",category);
        log.info("SEARCHARTICLE_INPUT_PARAM_{}",keyword);
        log.info("SEARCHARTICLE_INPUT_PARAM_{}",target);
        log.info("SEARCHARTICLE_INPUT_PARAM_{}",sort);
        log.info("SEARCHARTICLE_INPUT_PARAM_{}",page);
        log.info("SEARCHARTICLE_INPUT_PARAM_{}",size);

        ResponseMultiplePaging responseMultiplePaging = articleService.searchArticleAsPaging(category, keyword, target, sort, page, size);

        log.info("SEARCHARTICLE_OUTPUT_{}",responseMultiplePaging);
        return new ResponseEntity(responseMultiplePaging, HttpStatus.OK);
    }

    @GetMapping("/{article-id}")
    public ResponseEntity findDetailArticle(@RequestUser UserDto.UserInfo userInfo,
                                          @PathVariable("article-id") Long articleId) {
        log.info("FIND_DETAIL_ARTICLE_INPUT_{}", articleId);
        log.info("FIND_DETAIL_ARTICLE_USERINFO_INPUT_{}", userInfo);
        ArticleDto.ResponseDetailArticle result = articleService.findDetailArticle(articleId, userInfo);
        log.info("FIND_DETAIL_ARTICLE_OUTPUT_{}", result);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @PostMapping("/{article-id}/likes")
    public ResponseEntity pressLikeButton(@RequestUser UserDto.UserInfo userInfo,
                                          @PathVariable("article-id") Long articleId) {
        log.info("PRESS_LIKE_BUTTON_USERINFO_INPUT_{}",userInfo);
        log.info("PRESS_LIKE_BUTTON_INPUT_{}",articleId);
        ArticleDto.ResponseArticleLike responseArticleLike = articleService.pressLikeButton(articleId, userInfo);
        log.info("PRESS_LIKE_BUTTON_OUTPUT_{}", responseArticleLike);
        return new ResponseEntity(responseArticleLike, HttpStatus.OK);
    }

    @PostMapping("/{article-id}/reports")
    public ResponseEntity reportArticle(@PathVariable("article-id") Long articleId,
                                        @RequestBody ArticleDto.RequestReportArticle reportArticle,
                                        @RequestUser UserDto.UserInfo userInfo) {
        ArticleDto.ResponseReportArticle responseReportArticle =
                articleService.reportArticle(articleId, userInfo, articleMapper.requestReportArticleToReport(reportArticle));

        return new ResponseEntity(responseReportArticle, HttpStatus.CREATED);
    }

}
