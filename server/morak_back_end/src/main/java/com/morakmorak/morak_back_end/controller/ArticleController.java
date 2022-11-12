package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.BookmarkDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Bookmark;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("/{article-id}/bookmark")
    @ResponseStatus(HttpStatus.OK)
    public BookmarkDto.ResponsePostBookmark postOrDeleteBookmark(
            @RequestUser UserDto.UserInfo user,
            @PathVariable("article-id") Long articleId) {

        BookmarkDto.ResponsePostBookmark response =
                articleService.pressBookmark(user.getId(),articleId);
        return response;

    }

}
