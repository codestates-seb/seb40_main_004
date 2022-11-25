package com.morakmorak.morak_back_end.domain;

import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.util.TestConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class NotificationGeneratorTest {
    NotificationMessage notificationMessage = new NotificationMessageSpy();
    NotificationUri notificationUri = new NotificationUriSpy();

    static class NotificationMessageSpy extends NotificationMessage {

        @Override
        String generateMessage(User sender, Answer answer) {
            return CONTENT1;
        }

        @Override
        String generateMessage(User sender, Comment comment) {
            return CONTENT1;
        }

        @Override
        String generateMessage(ArticleLike articleLike, int count) {
            return CONTENT1;
        }

        @Override
        String generateMessage(AnswerLike answerLike, int count) {
            return CONTENT1;
        }

        @Override
        String generateMessage(Review review) {
            return CONTENT1;
        }
    }

    static class NotificationUriSpy extends NotificationUri {

        @Override
        String generateUri(ArticleLike articleLike) {
            return CONTENT2;
        }

        @Override
        String generateUri(AnswerLike answerLike) {
            return CONTENT2;
        }

        @Override
        String generateUri(Comment comment) {
            return CONTENT2;
        }

        @Override
        String generateUri(Answer answer) {
            return CONTENT2;
        }

        @Override
        String generateUri(Review review) {
            return CONTENT2;
        }
    }

    User sender;
    User receiver;

    @BeforeEach
    void init() {
        sender = User.builder().nickname(NICKNAME1).build();
        receiver = User.builder().id(ID1).nickname(NICKNAME2).build();
    }

    @Test
    @DisplayName("answer가 주어졌을 때 article 작성자의 id를 가진 notification 반환")
    void generateNotification1() {
        //given
        Article article = Article.builder().user(receiver).build();
        Answer answer = Answer.builder().article(article).build();

        NotificationGenerator generator = NotificationGenerator.of(sender, answer);
        ReflectionTestUtils.setField(generator, "notificationMessage", notificationMessage);
        ReflectionTestUtils.setField(generator, "notificationUri", notificationUri);

        //when
        Notification notification = generator.generateNotification();

        //then
        Assertions.assertThat(notification.getUser().getNickname()).isEqualTo(receiver.getNickname());
    }

    @Test
    @DisplayName("comment가 주어졌을 때 article 작성자의 id를 가진 notification 반환(article의 comment인 경우)")
    void generateNotification2() {
        //given
        Article article = Article.builder().user(receiver).build();
        Comment comment = Comment.builder().article(article).build();

        NotificationGenerator generator = NotificationGenerator.of(sender, comment);
        ReflectionTestUtils.setField(generator, "notificationMessage", notificationMessage);
        ReflectionTestUtils.setField(generator, "notificationUri", notificationUri);

        //when
        Notification notification = generator.generateNotification();

        //then
        Assertions.assertThat(notification.getUser().getNickname()).isEqualTo(receiver.getNickname());
    }

    @Test
    @DisplayName("comment가 주어졌을 때 article 작성자의 id를 가진 notification 반환(answer의 comment인 경우)")
    void generateNotification3() {
        //given
        Answer answer = Answer.builder().user(receiver).build();
        Comment comment = Comment.builder().answer(answer).build();

        NotificationGenerator generator = NotificationGenerator.of(sender, comment);
        ReflectionTestUtils.setField(generator, "notificationMessage", notificationMessage);
        ReflectionTestUtils.setField(generator, "notificationUri", notificationUri);

        //when
        Notification notification = generator.generateNotification();

        //then
        Assertions.assertThat(notification.getUser().getNickname()).isEqualTo(receiver.getNickname());
    }

    @Test
    @DisplayName("ArticleLike와 count가 주어졌을때 receiver를 가진 notification 반환")
    void generateNotification4() {
        //given
        Article article = Article.builder().user(receiver).build();
        ArticleLike articleLike = ArticleLike.builder().article(article).build();

        NotificationGenerator generator = NotificationGenerator.of(articleLike, 10);
        ReflectionTestUtils.setField(generator, "notificationMessage", notificationMessage);
        ReflectionTestUtils.setField(generator, "notificationUri", notificationUri);

        //when
        Notification notification = generator.generateNotification();

        //then
        Assertions.assertThat(notification.getUser().getNickname()).isEqualTo(receiver.getNickname());
    }

    @Test
    @DisplayName("AnswerLike와 count가 주어졌을때 receiver를 가진 notification 반환")
    void generateNotification5() {
        //given
        Answer answer = Answer.builder().user(receiver).build();
        AnswerLike answerLike = AnswerLike.builder().answer(answer).build();

        NotificationGenerator generator = NotificationGenerator.of(answerLike, 10);
        ReflectionTestUtils.setField(generator, "notificationMessage", notificationMessage);
        ReflectionTestUtils.setField(generator, "notificationUri", notificationUri);

        //when
        Notification notification = generator.generateNotification();

        //then
        Assertions.assertThat(notification.getUser().getNickname()).isEqualTo(receiver.getNickname());
    }

    @Test
    @DisplayName("review가 주어졌을때 receiver를 가진 notification 반환")
    void generateNotification6() {
        //given
        Review review = Review.builder().receiver(receiver).build();

        NotificationGenerator generator = NotificationGenerator.of(review);
        ReflectionTestUtils.setField(generator, "notificationMessage", notificationMessage);
        ReflectionTestUtils.setField(generator, "notificationUri", notificationUri);

        //when
        Notification notification = generator.generateNotification();

        //then
        Assertions.assertThat(notification.getUser().getNickname()).isEqualTo(receiver.getNickname());
    }
}