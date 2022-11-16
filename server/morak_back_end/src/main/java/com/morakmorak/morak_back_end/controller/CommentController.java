package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Comment;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/articles/{article-id}")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto.ReponsePost requestPostComment(@RequestBody @Valid CommentDto.RequestPost request,
                                                 @PathVariable("article-id") Long articleId,
                                                 @RequestUser UserDto.UserInfo user) {
        Comment commentNotSaved = Comment.builder().content(request.getContent()).build();
        CommentDto.ReponsePost madeComment = commentService.makeComment(user.getId(), articleId, commentNotSaved);
        return madeComment;
    }
}
