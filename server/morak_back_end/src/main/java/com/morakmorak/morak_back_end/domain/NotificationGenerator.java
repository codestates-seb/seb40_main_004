package com.morakmorak.morak_back_end.domain;

import com.morakmorak.morak_back_end.entity.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public final class NotificationGenerator {
    @Builder.Default
    private final NotificationMessage notificationMessage = new NotificationMessage();
    @Builder.Default
    private final NotificationUri notificationUri = new NotificationUri();

    private Answer answer;
    private ArticleLike articleLike;
    private AnswerLike answerLike;
    private Comment comment;
    private Review review;
    private User sender;
    private Integer likeCount;

    public static NotificationGenerator of(User sender, Answer answer) {
        return NotificationGenerator.builder()
                .sender(sender)
                .answer(answer)
                .build();
    }

    public static NotificationGenerator of(User sender, Comment comment) {
        return NotificationGenerator.builder()
                .sender(sender)
                .comment(comment)
                .build();
    }

    public static NotificationGenerator of(ArticleLike articleLike, Integer likeCount) {
        return NotificationGenerator.builder()
                .articleLike(articleLike)
                .likeCount(likeCount)
                .build();
    }

    public static NotificationGenerator of(AnswerLike answerLike, Integer likeCount) {
        return NotificationGenerator.builder()
                .answerLike(answerLike)
                .likeCount(likeCount)
                .build();
    }

    public static NotificationGenerator of(Review review) {
        return NotificationGenerator.builder()
                .review(review)
                .build();
    }

    public Notification generateNotification() {
        String message = null;
        String uri = null;
        User receiver = null;

        if (answer != null) {
            message = notificationMessage.generateMessage(sender, answer);
            uri = notificationUri.generateUri(answer);
            receiver = answer.getArticle().getUser();
        }

        if (articleLike != null) {
            message = notificationMessage.generateMessage(articleLike, likeCount);
            uri = notificationUri.generateUri(articleLike);
            receiver = articleLike.getArticle().getUser();
        }

        if (answerLike != null) {
            message = notificationMessage.generateMessage(answerLike, likeCount);
            uri = notificationUri.generateUri(answerLike);
            receiver = answerLike.getAnswer().getUser();
        }

        if (comment != null) {
            message = notificationMessage.generateMessage(sender, comment);
            uri = notificationUri.generateUri(comment);

            if (comment.getAnswer() == null) {
                receiver = comment.getArticle().getUser();
            } else {
                receiver = comment.getAnswer().getUser();
            }
        }

        if (review != null) {
            message = notificationMessage.generateMessage(review);
            uri = notificationUri.generateUri(review);
            receiver = review.getReceiver();
        }

        if (!StringUtils.hasText(message) || !StringUtils.hasText(uri) || receiver == null) throw new IllegalArgumentException("유효하지 않은 요청");


        Notification notification = Notification.of(message, uri, receiver);
        receiver.addNotification(notification);

        return notification;
    }
}
