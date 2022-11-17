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
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;
    private final FileMapper fileMapper;
    private final CategoryMapper categoryMapper;


    @PostMapping
    public ResponseEntity uploadArticle(@Valid @RequestBody ArticleDto.RequestUploadArticle requestUploadArticle,
                                        @RequestUser UserDto.UserInfo userInfo) {
        Article article = articleMapper.requestUploadArticleToEntity(requestUploadArticle);
        List<TagDto.SimpleTag> tags = tagMapper.requestTagWithIdAndNameToTagDto(requestUploadArticle);
        List<FileDto.RequestFileWithId> files = fileMapper.RequestFileWithIdToFile(requestUploadArticle);
        Category category = categoryMapper.RequestUploadArticleToCategory(requestUploadArticle);
        ArticleDto.ResponseSimpleArticle upload = articleService.upload(article, userInfo, tags, files, category);

        return new ResponseEntity<>(upload, HttpStatus.CREATED);
    }

    @PatchMapping("/{article-id}")
    public ResponseEntity updateArticle(@Valid @RequestBody ArticleDto.RequestUpdateArticle requestUpdateArticle,
                                        @RequestUser UserDto.UserInfo userInfo,
                                        @PathVariable("article-id") Long articleId) {

        Article article = articleMapper.requestUpdateArticleToEntity(requestUpdateArticle, articleId);
        List<TagDto.SimpleTag> tags = tagMapper.requestTagWithIdAndNameToTagDto(requestUpdateArticle);
        List<FileDto.RequestFileWithId> files = fileMapper.RequestFileWithIdToFile(requestUpdateArticle);
        ArticleDto.ResponseSimpleArticle update = articleService.update(article, userInfo, tags, files);
        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    @DeleteMapping("/{article-id}")
    public ResponseEntity deleteArticle(@PathVariable("article-id") Long articleId,
                                        @RequestUser UserDto.UserInfo userInfo) {
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
        ResponseMultiplePaging responseMultiplePaging = articleService.searchArticleAsPaging(category, keyword, target, sort, page, size);


        return new ResponseEntity(responseMultiplePaging, HttpStatus.OK);
    }

    @GetMapping("/{article-id}")
    public ResponseEntity findDetailArticle(@RequestUser UserDto.UserInfo userInfo,
                                          @PathVariable("article-id") Long articleId) {
        ArticleDto.ResponseDetailArticle result = articleService.findDetailArticle(articleId, userInfo);
        return new ResponseEntity(result, HttpStatus.OK);
    }

}
