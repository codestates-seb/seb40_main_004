package com.morakmorak.morak_back_end.domain;

import com.morakmorak.morak_back_end.entity.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.morakmorak.morak_back_end.util.TestConstants.*;

class NotificationMessageTest {
    NotificationMessage notificationMessage = new NotificationMessage();

    @Test
    void generateAnswerNotification() {
        // given
        User user = User.builder().nickname(NICKNAME1).build();
        Article article = Article.builder().title(CONTENT1).build();
        Answer answer = Answer.builder().article(article).build();

        // when
        String message = notificationMessage.generateMessage(user, answer);

        // then
        Assertions.assertThat(message).isEqualTo("회원님께서 작성하신 \"" + CONTENT1 + "\"에 " + user.getNickname() + "님께서 답변해주셨어요.");
    }

    @Test
    @DisplayName("부모가 article인 경우")
    void generateCommentNotification() {
        // given
        User user = User.builder().nickname(NICKNAME1).build();
        Article article = Article.builder().title(CONTENT1).build();
        Comment comment = Comment.builder().article(article).build();

        // when
        String message = notificationMessage.generateMessage(user, comment);

        // then
        Assertions.assertThat(message).isEqualTo("회원님께서 작성하신 \"" + CONTENT1 + "\"에 " + user.getNickname() + "님께서 댓글을 남기셨어요.");
    }

    @Test
    @DisplayName("부모가 답변인 경우")
    void testGenerateCommentNotification() {
        // given
        User user = User.builder().nickname(NICKNAME1).build();
        Article article = Article.builder().title(CONTENT1).build();
        Answer answer = Answer.builder().article(article).build();
        Comment comment = Comment.builder().answer(answer).build();

        // when
        String message = notificationMessage.generateMessage(user, comment);

        // then
        Assertions.assertThat(message).isEqualTo("회원님께서 작성하신 \"" + CONTENT1 + "\"의 답변에 " + user.getNickname() + "님께서 댓글을 남기셨어요.");
    }

    @Test
    @DisplayName("게시글에 대한 좋아요인 경우")
    void generateLikeNotification() {
        // given
        Article article = Article.builder().title(CONTENT1).build();
        ArticleLike articleLike = ArticleLike.builder().article(article).build();

        // when
        String message = notificationMessage.generateMessage(articleLike, 10);

        // then
        Assertions.assertThat(message).isEqualTo("회원님께서 작성하신 \"" + CONTENT1 + "\"의 좋아요가 " + 10 + "개를 돌파했어요.");
    }

    @Test
    @DisplayName("답글에 대한 좋아요인 경우")
    void testGenerateLikeNotification() {
        // given
        Article article = Article.builder().title(CONTENT1).build();
        Answer answer = Answer.builder().article(article).build();
        AnswerLike answerLike = AnswerLike.builder().answer(answer).build();

        // when
        String message = notificationMessage.generateMessage(answerLike, 10);

        // then
        Assertions.assertThat(message).isEqualTo("회원님께서 작성하신 \"" + CONTENT1 + "\"의 답변에 대한 좋아요가 " + 10 + "개를 돌파했어요.");
    }

    @Test
    @DisplayName("답글 채택인 경우")
    void test() {
        // given
        User sender = User.builder().nickname(NICKNAME1).build();
        Article article = Article.builder().title(CONTENT1).build();
        Answer answer = Answer.builder().article(article).build();
        Review review = Review.builder().answer(answer).sender(sender).build();

        // when
        String message = notificationMessage.generateMessage(review);

        // then
        Assertions.assertThat(message).isEqualTo(sender.getNickname() + "님께서 회원님이 작성하신 " + article.getTitle() + "의 답변을 채택하셨어요!");
    }

    @Test
    @DisplayName("단순 후원인 경우")
    void test2() {
        // given
        User sender = User.builder().nickname(NICKNAME1).build();
        Review review = Review.builder().sender(sender).point(100).build();

        // when
        String message = notificationMessage.generateMessage(review);

        // then
        Assertions.assertThat(message).isEqualTo(sender.getNickname() + "님께서 회원님께 " + review.getPoint() + "포인트를 후원하셨어요!");
    }
}