package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Comment;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.CommentRepository;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleService articleService;
    private final UserService userService;

    public CommentDto.Response makeComment(Long userId, Long articleId, Comment commentNotSaved) {
        User verifiedUser = userService.findVerifiedUserById(userId);
        Article verifiedArticle = articleService.findVerifiedArticle(articleId);

        commentNotSaved.injectUser(verifiedUser).injectArticle(verifiedArticle);
        Comment savedComment = commentRepository.save(commentNotSaved);
        return CommentDto.Response.of(Optional.of(savedComment));
    }

    public List<CommentDto.Response> editComment(Long userId, Long articleId, Long commentId, String newContent) throws Exception {
        User verifiedUser = userService.findVerifiedUserById(userId);
        Article verifiedArticle = articleService.findVerifiedArticle(articleId);
        Comment foundComment = findVerifiedCommentById(commentId);

        if (foundComment.hasPermissionWith(verifiedUser) && verifiedArticle.statusIsPosting()) {
            foundComment.updateContent(newContent);
            return findAllComments(articleId);
        } else {
            throw new BusinessLogicException(ErrorCode.CANNOT_ACCESS_COMMENT);
        }
    }


    public Comment findVerifiedCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new BusinessLogicException(ErrorCode.COMMENT_NOT_FOUND));
    }

    public List<CommentDto.Response> findAllComments(Long articleId) {
        return commentRepository.findAllCommentsByArticleId(articleId).stream().map(comment -> CommentDto.Response.of(Optional.of(comment))).collect(Collectors.toList());
    }

    public boolean deleteComment(Long userId, Long articleId, Long commentId) throws Exception {
        User verifiedUser = userService.findVerifiedUserById(userId);
        Article verifiedArticle = articleService.findVerifiedArticle(articleId);
        Comment foundComment = findVerifiedCommentById(commentId);
        if (foundComment.hasPermissionWith(verifiedUser) && verifiedArticle.statusIsPosting()) {
            commentRepository.deleteById(commentId);
            return true;
        } else {
            throw new BusinessLogicException(ErrorCode.CANNOT_ACCESS_COMMENT);
        }
    }
}
