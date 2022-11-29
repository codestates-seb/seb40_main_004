package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.BookmarkDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
@Slf4j
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping("/{article-id}/bookmark")
    @ResponseStatus(HttpStatus.OK)
    public BookmarkDto.ResponsePostBookmark postOrDeleteBookmark(
            @RequestUser UserDto.UserInfo user,
            @PathVariable("article-id") Long articleId) {
        log.info("POST_OR_DELETE_BOOKMARK_INPUT", user);
        log.info("POST_OR_DELETE_BOOKMARK_INPUT", articleId);
        BookmarkDto.ResponsePostBookmark response =
                bookmarkService.pressBookmark(user.getId(),articleId);
        return response;

    }

}