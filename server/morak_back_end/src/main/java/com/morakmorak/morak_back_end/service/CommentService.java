package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.domain.NotificationGenerator;
import com.morakmorak.morak_back_end.domain.PointCalculator;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.CommentRepository;
import com.morakmorak.morak_back_end.repository.notification.NotificationRepository;
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
    private final AnswerService answerService;
    private final UserService userService;
    private final NotificationRepository notificationRepository;
    private final PointCalculator pointCalculator;

    public List<CommentDto.Response> makeComment(Long userId, Long targetId, Comment commentNotSaved, boolean isArticle) {
        User verifiedUser = userService.findVerifiedUserById(userId);

        if (isArticle) {
            Long articleId = targetId;
            Article verifiedArticle = articleService.findVerifiedArticle(articleId);

            commentNotSaved.injectUser(verifiedUser).injectArticle(verifiedArticle);
            Comment savedComment = commentRepository.save(commentNotSaved);

            sendNotificationByComment(verifiedUser, savedComment);
            verifiedUser.addPoint(savedComment, pointCalculator);

            return findAllCommentsBy(verifiedArticle);
        }
        Long answerId = targetId;
        Answer verifiedAnswer = answerService.findVerifiedAnswerById(answerId);
        commentNotSaved.injectUser(verifiedUser).injectAnswer(verifiedAnswer);
        Comment savedComment = commentRepository.save(commentNotSaved);

        sendNotificationByComment(verifiedUser, savedComment);
        verifiedUser.addPoint(savedComment, pointCalculator);

        return findAllCommentsBy(verifiedAnswer);

    }

    public List<CommentDto.Response> editComment(Long userId, Long targetId, Long commentId, String newContent, boolean isArticle) throws Exception {
        User verifiedUser = userService.findVerifiedUserById(userId);
        Comment foundComment = findVerifiedCommentById(commentId);
        checkUserPermission(foundComment, verifiedUser);

        if (isArticle) {
            Long articleId = targetId;
            Article verifiedArticle = articleService.findVerifiedArticle(articleId);
            checkArticleStatus(verifiedArticle);
            foundComment.updateContent(newContent);
            return findAllCommentsBy(verifiedArticle);
        }
        Long answerId = targetId;
        Answer verifiedAnswer = answerService.findVerifiedAnswerById(answerId);
        checkArticleStatus(verifiedAnswer.getArticle());
        foundComment.updateContent(newContent);
        return findAllCommentsBy(verifiedAnswer);
    }

    private void checkUserPermission(Comment comment, User requestUser) {
        if (!comment.hasPermissionWith(requestUser)) {
            throw new BusinessLogicException(ErrorCode.INVALID_USER);
        }
    }

    public List<CommentDto.Response> deleteComment(Long userId, Long targetId, Long commentId, boolean isArticle) throws Exception {
        User verifiedUser = userService.findVerifiedUserById(userId);
        Comment foundComment = findVerifiedCommentById(commentId);
        checkUserPermission(foundComment, verifiedUser);

        if (isArticle) {
            Long articleId = targetId;
            Article verifiedArticle = articleService.findVerifiedArticle(articleId);
            checkArticleStatus(verifiedArticle);
            commentRepository.deleteById(commentId);
            verifiedUser.minusPoint(foundComment, pointCalculator);
            return findAllCommentsBy(verifiedArticle);
        }
        Long answerId = targetId;
        Answer verifiedAnswer = answerService.findVerifiedAnswerById(answerId);
        checkArticleStatus(verifiedAnswer.getArticle());
        commentRepository.deleteById(commentId);
        verifiedUser.minusPoint(foundComment, pointCalculator);
        return findAllCommentsBy(verifiedAnswer);
    }

    public List<CommentDto.Response> findAllComments(Long targetId, boolean isArticle) {
        if (isArticle) {
            Long articleId = targetId;
            Article verifiedArticle = articleService.findVerifiedArticle(articleId);
            checkArticleStatus(verifiedArticle);
            return findAllCommentsBy(verifiedArticle);
        }
        Long answerId = targetId;
        Answer verifiedAnswer = answerService.findVerifiedAnswerById(answerId);
        checkArticleStatus(verifiedAnswer.getArticle());
        return findAllCommentsBy(verifiedAnswer);
    }

    public List<CommentDto.Response> findAllCommentsBy(Article article) {
        return commentRepository.findAllCommentsByArticleId(article.getId()).stream().map(comment -> CommentDto.Response.ofArticle(Optional.of(comment))).collect(Collectors.toList());
    }

    public List<CommentDto.Response> findAllCommentsBy(Answer answer) {
        return commentRepository.findAllCommentsByAnswerId(answer.getId()).stream().map(comment -> CommentDto.Response.ofAnswer(comment)).collect(Collectors.toList());
    }

    private void sendNotificationByComment(User receiver, Comment comment) {
        NotificationGenerator generator = NotificationGenerator.of(receiver, comment);
        Notification notification = generator.generateNotification();
        notificationRepository.save(notification);
    }
    public Comment findVerifiedCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new BusinessLogicException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void checkArticleStatus(Article article) {
        if (!article.statusIsPosting()) {
            throw new BusinessLogicException(ErrorCode.NO_ACCESS_TO_THAT_OBJECT);
        }
    }


}
