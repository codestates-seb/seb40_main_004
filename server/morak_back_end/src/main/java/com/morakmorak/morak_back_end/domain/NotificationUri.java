package com.morakmorak.morak_back_end.domain;

import com.morakmorak.morak_back_end.entity.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
class NotificationUri {
    private final StringBuilder stringBuilder = new StringBuilder();

    String generateUri(ArticleLike articleLike) {
        String uri = stringBuilder
                .append("/articles/")
                .append(articleLike.getArticle().getId())
                .toString();

        cleanBuilder();
        return uri;
    }

    String generateUri(AnswerLike answerLike) {
        String uri = stringBuilder
                .append("/articles/")
                .append(answerLike.getAnswer().getArticle().getId())
                .toString();

        cleanBuilder();
        return uri;
    }

    String generateUri(Comment comment) {
        Long articleId = (comment.getAnswer() == null) ? comment.getArticle().getId() : comment.getAnswer().getArticle().getId();

        String uri = stringBuilder
                .append("/articles/")
                .append(articleId)
                .toString();

        cleanBuilder();
        return uri;
    }

    String generateUri(Answer answer) {
        String uri = stringBuilder
                .append("/articles/")
                .append(answer.getArticle().getId())
                .toString();

        cleanBuilder();
        return uri;
    }

    String generateUri(Review review) {
        String uri;

        if (review.getAnswer() == null) {
            Long userId = review.getReceiver().getId();

            uri = stringBuilder.append("/users/")
                    .append(userId)
                    .append("/dashboard")
                    .toString();
        } else {
            Long articleId = review.getAnswer().getArticle().getId();

            uri = stringBuilder.append("/articles/")
                    .append(articleId)
                    .toString();
        }

        return uri;
    }

    private void cleanBuilder() {
        stringBuilder.delete(0, stringBuilder.length());
    }
}
