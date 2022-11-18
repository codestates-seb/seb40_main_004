package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Comment;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.morakmorak.morak_back_end.exception.ErrorCode.ARTICLE_NOT_FOUND;

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
        return CommentDto.Response.of(savedComment);
    }

    public List<CommentDto.Response> editComment(Long userId, Long articleId, Long commentId, String newContent) throws Exception {
        User verifiedUser = userService.findVerifiedUserById(userId);
        Article verifiedArticle = articleService.findVerifiedArticle(articleId);
        Comment foundComment = findVerifiedCommentById(commentId);

        if (foundComment.hasPermissionWith(verifiedUser) && isEditableStatus(verifiedArticle)) {
            foundComment.updateContent(newContent);
            return findAllComments(articleId);
        } else {
            throw new BusinessLogicException(ErrorCode.CANNOT_ACCESS_COMMENT);
        }
    }

    public boolean isEditableStatus(Article verifiedArticle) throws Exception {
        if (verifiedArticle.getArticleStatus() != ArticleStatus.POSTING) {
            throw new BusinessLogicException(ARTICLE_NOT_FOUND);
        } else {
            return true;
        }
    }

    public Comment findVerifiedCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new BusinessLogicException(ErrorCode.COMMENT_NOT_FOUND));
    }

    public List<CommentDto.Response> findAllComments(Long articleId) {
        return commentRepository.findAllCommentsByArticleId(articleId).stream().map(comment -> CommentDto.Response.of(comment)).collect(Collectors.toList());
    }

    public boolean deleteComment(Long userId, Long articleId, Long commentId) throws Exception {
        User verifiedUser = userService.findVerifiedUserById(userId);
        Article verifiedArticle = articleService.findVerifiedArticle(articleId);
        Comment foundComment = findVerifiedCommentById(commentId);
        if (foundComment.hasPermissionWith(verifiedUser) && isEditableStatus(verifiedArticle)) {
            commentRepository.deleteById(commentId);
            return true;
        } else {
            throw new BusinessLogicException(ErrorCode.CANNOT_ACCESS_COMMENT);
        }
    }
}
