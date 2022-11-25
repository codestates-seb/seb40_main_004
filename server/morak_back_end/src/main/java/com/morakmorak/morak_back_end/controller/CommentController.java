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
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/articles/{article-id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public List<CommentDto.Response> requestPostCommentOnArticle(@RequestBody @Valid CommentDto.Request request,
                                                           @PathVariable("article-id") Long articleId,
                                                           @RequestUser UserDto.UserInfo user) {
        Comment commentNotSaved = Comment.builder().content(request.getContent()).build();
        return commentService.makeComment(user.getId(), articleId,commentNotSaved, true);
    }

    @PatchMapping("/articles/{article-id}/comments/{comment-id}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto.Response> requestUpdateCommentOnArticle(@RequestBody @Valid CommentDto.Request request,
                                                          @PathVariable("article-id") Long articleId,
                                                          @PathVariable("comment-id") Long commentId,
                                                          @RequestUser UserDto.UserInfo user) throws Exception {

        return commentService.editComment(user.getId(), articleId, commentId, request.getContent(),true);
    }

    @DeleteMapping("/articles/{article-id}/comments/{comment-id}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto.Response> requestDeleteOnArticle(@PathVariable("article-id") Long articleId,
                                                          @PathVariable("comment-id") Long commentId,
                                                          @RequestUser UserDto.UserInfo user) throws Exception {
        return commentService.deleteComment(user.getId(), articleId, commentId,true);
    }

    @GetMapping("/articles/{article-id}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto.Response> requestGetCommentsByArticle(@PathVariable("article-id") Long articleId,
                                                                 @Positive @RequestParam(value = "page",defaultValue = "0") int page,
                                                                 @Positive @RequestParam(value = "size",defaultValue = "10") int size) {
        return commentService.findAllComments(articleId, true);
    }

    @PostMapping("/answers/{answer-id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public List<CommentDto.Response> requestPostCommentOnAnswer(@RequestBody @Valid CommentDto.Request request,
                                                           @PathVariable("answer-id") Long answerId,
                                                           @RequestUser UserDto.UserInfo user) {
        Comment commentNotSaved = Comment.builder().content(request.getContent()).build();
        return commentService.makeComment(user.getId(), answerId, commentNotSaved,false);
    }

    @PatchMapping("/answers/{answer-id}/comments/{comment-id}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto.Response> requestUpdateCommentOnAnswer(@RequestBody @Valid CommentDto.Request request,
                                                          @PathVariable("answer-id") Long articleId,
                                                          @PathVariable("comment-id") Long commentId,
                                                          @RequestUser UserDto.UserInfo user) throws Exception {

        return commentService.editComment(user.getId(), articleId, commentId, request.getContent(),false);
    }

    @DeleteMapping("/answers/{answer-id}/comments/{comment-id}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto.Response> requestDeleteCommentOnAnswer(@PathVariable("answer-id") Long answerId,
                                                          @PathVariable("comment-id") Long commentId,
                                                          @RequestUser UserDto.UserInfo user) throws Exception {
        return commentService.deleteComment(user.getId(), answerId, commentId,false);

    }
    @GetMapping("/answers/{answer-id}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto.Response> requestGetCommentsByAnswer(@PathVariable("answer-id") Long articleId,
                                                                 @Positive @RequestParam(value = "page",defaultValue = "0") int page,
                                                                 @Positive @RequestParam(value = "size",defaultValue = "10") int size) {
        return commentService.findAllComments(articleId,false);
    }
}
