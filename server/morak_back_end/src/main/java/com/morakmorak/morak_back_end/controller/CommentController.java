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
@RequestMapping("")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/articles/{article-id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto.Response requestPostComment(@RequestBody @Valid CommentDto.Request request,
                                                  @PathVariable("article-id") Long articleId,
                                                  @RequestUser UserDto.UserInfo user) {
        Comment commentNotSaved = Comment.builder().content(request.getContent()).build();
        CommentDto.Response madeComment = commentService.makeComment(user.getId(), articleId, commentNotSaved);
        return madeComment;
    }

    @PatchMapping("/articles/{article-id}/comments/{comment-id}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto.Response> requestUpdateComment(@RequestBody @Valid CommentDto.Request request,
                                                          @PathVariable("article-id") Long articleId,
                                                          @PathVariable("comment-id") Long commentId,
                                                          @RequestUser UserDto.UserInfo user) throws Exception {

        return commentService.editComment(user.getId(), articleId, commentId, request.getContent());
    }

    @DeleteMapping("/articles/{article-id}/comments/{comment-id}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto.Response> requestDeleteComment(@PathVariable("article-id") Long articleId,
                                                          @PathVariable("comment-id") Long commentId,
                                                          @RequestUser UserDto.UserInfo user) throws Exception {
        commentService.deleteComment(user.getId(), articleId, commentId);
        return commentService.findAllComments(articleId);
    }
    @GetMapping("/articles/{article-id}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto.Response> requestGetCommentsByArticle(@PathVariable("article-id") Long articleId,
                                                                 @Positive @RequestParam(value = "page",defaultValue = "0") int page,
                                                                 @Positive @RequestParam(value = "size",defaultValue = "10") int size) {
        return commentService.findAllComments(articleId);
    }
    /*@PostMapping("/articles/{answer-id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto.Response requestPostComment(@RequestBody @Valid CommentDto.Request request,
                                                  @PathVariable("article-id") Long articleId,
                                                  @RequestUser UserDto.UserInfo user) {
        Comment commentNotSaved = Comment.builder().content(request.getContent()).build();
        CommentDto.Response madeComment = commentService.makeComment(user.getId(), articleId, commentNotSaved);
        return madeComment;
    }

    @PatchMapping("/articles/{answer-id}/comments/{comment-id}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto.Response> requestUpdateComment(@RequestBody @Valid CommentDto.Request request,
                                                          @PathVariable("article-id") Long articleId,
                                                          @PathVariable("comment-id") Long commentId,
                                                          @RequestUser UserDto.UserInfo user) throws Exception {

        return commentService.editComment(user.getId(), articleId, commentId, request.getContent());
    }

    @DeleteMapping("/articles/{answer-id}/comments/{comment-id}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto.Response> requestDeleteComment(@PathVariable("article-id") Long articleId,
                                                          @PathVariable("comment-id") Long commentId,
                                                          @RequestUser UserDto.UserInfo user) throws Exception {
        commentService.deleteComment(user.getId(), articleId, commentId);
        return commentService.findAllComments(articleId);
    }
    @GetMapping("/answers/{answer-id}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto.Response> requestGetCommentsByAnswer(@PathVariable("article-id") Long articleId,
                                                                 @Positive @RequestParam(value = "page",defaultValue = "0") int page,
                                                                 @Positive @RequestParam(value = "size",defaultValue = "10") int size) {
        return commentService.findAllComments(articleId);
    }*/
}
