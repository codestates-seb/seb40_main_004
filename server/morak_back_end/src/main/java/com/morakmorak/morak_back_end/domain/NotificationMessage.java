package com.morakmorak.morak_back_end.domain;

import com.morakmorak.morak_back_end.entity.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
class NotificationMessage {
    private final StringBuilder stringBuilder = new StringBuilder();

    String generateMessage(User sender, Answer answer) {
        String message = stringBuilder.append("회원님께서 작성하신 ")
                .append("\"")
                .append(answer.getArticle().getTitle())
                .append("\"")
                .append("에 ")
                .append(sender.getNickname())
                .append("님께서 답변해주셨어요.")
                .toString();

        cleanBuilder();
        return message;
    }

    String generateMessage(User sender, Comment comment) {
        String message;

        if (comment.getAnswer() == null) {
            message = stringBuilder.append("회원님께서 작성하신 ")
                    .append("\"")
                    .append(comment.getArticle().getTitle())
                    .append("\"")
                    .append("에 ")
                    .append(sender.getNickname())
                    .append("님께서 댓글을 남기셨어요.")
                    .toString();
        } else {
            message = stringBuilder.append("회원님께서 작성하신 ")
                    .append("\"")
                    .append(comment.getAnswer().getArticle().getTitle())
                    .append("\"")
                    .append("의 답변에 ")
                    .append(sender.getNickname())
                    .append("님께서 댓글을 남기셨어요.")
                    .toString();
        }

        cleanBuilder();
        return message;
    }

    String generateMessage(ArticleLike articleLike, int count) {
        verifyCount(count);

        String result = stringBuilder.append("회원님께서 작성하신 ")
                .append("\"")
                .append(articleLike.getArticle().getTitle())
                .append("\"")
                .append("의 좋아요가 ")
                .append(count)
                .append("개를 돌파했어요.")
                .toString();

        cleanBuilder();
        return result;
    }

    String generateMessage(AnswerLike answerLike, int count) {
        verifyCount(count);

        String result = stringBuilder.append("회원님께서 작성하신 ")
                .append("\"")
                .append(answerLike.getAnswer().getArticle().getTitle())
                .append("\"")
                .append("의 답변에 대한 좋아요가 ")
                .append(count)
                .append("개를 돌파했어요.")
                .toString();

        cleanBuilder();
        return result;
    }

    String generateMessage(Review review) {
        Integer point = review.getPoint();
        User sender = review.getSender();
        String message;

        if (review.getAnswer() == null) {
            message = stringBuilder.append(sender.getNickname())
                    .append("님께서 회원님께 ")
                    .append(point)
                    .append("포인트를 후원하셨어요!")
                    .toString();
        } else {
            String title = review.getAnswer().getArticle().getTitle();
            message = stringBuilder.append(sender.getNickname())
                    .append("님께서 회원님이 작성하신 ")
                    .append(title)
                    .append("의 답변을 채택하셨어요!")
                    .toString();
        }

        cleanBuilder();
        return message;
    }

    private void cleanBuilder() {
        stringBuilder.delete(0, stringBuilder.length());
    }

    private void verifyCount(int count) {
        if (count % 10 != 0) throw new IllegalArgumentException("카운트는 항상 10으로 나누어 떨어져야 합니다.");
    }
}
