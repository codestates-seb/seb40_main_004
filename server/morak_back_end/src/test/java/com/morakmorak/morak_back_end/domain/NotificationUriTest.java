package com.morakmorak.morak_back_end.domain;

import com.morakmorak.morak_back_end.entity.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class NotificationUriTest {
    NotificationUri generator = new NotificationUri();

    @Test
    void generateArticleUri() {
        //given
        Article article = Article.builder().id(1L).build();
        Answer answer = Answer.builder().article(article).build();

        //when
        String result = generator.generateUri(answer);

        //then
        Assertions.assertThat(result).isEqualTo("/articles/1");
    }

    @Test
    void testGenerateArticleUri() {
        //given
        Article article = Article.builder().id(10L).build();
        Comment comment = Comment.builder().article(article).build();

        //when
        String result = generator.generateUri(comment);

        //then
        Assertions.assertThat(result).isEqualTo("/articles/10");
    }

    @Test
    void testGenerateArticleUri3() {
        //given
        Article article = Article.builder().id(100L).build();
        Answer answer = Answer.builder().article(article).build();
        Comment comment = Comment.builder().answer(answer).build();

        //when
        String result = generator.generateUri(comment);

        //then
        Assertions.assertThat(result).isEqualTo("/articles/100");
    }

    @Test
    void testGenerateArticleUri1() {
        //given
        Article article = Article.builder().id(1000L).build();
        ArticleLike articleLike = ArticleLike.builder().article(article).build();

        //when
        String result = generator.generateUri(articleLike);

        //then
        Assertions.assertThat(result).isEqualTo("/articles/1000");
    }

    @Test
    void testGenerateArticleUri2() {
        //given
        Article article = Article.builder().id(10000L).build();
        Answer answer = Answer.builder().article(article).build();
        AnswerLike answerLike = AnswerLike.builder().answer(answer).build();

        //when
        String result = generator.generateUri(answerLike);

        //then
        Assertions.assertThat(result).isEqualTo("/articles/10000");
    }

    @Test
    void testGenerateArticleUri4() {
        //given
        Article article = Article.builder().id(10000L).build();
        Answer answer = Answer.builder().article(article).build();
        Review review = Review.builder().answer(answer).build();

        //when
        String result = generator.generateUri(review);

        //then
        Assertions.assertThat(result).isEqualTo("/articles/10000");
    }

    @Test
    void testGenerateArticleUri5() {
        //given
        User user = User.builder().id(1L).build();
        Review review = Review.builder().receiver(user).build();

        //when
        String result = generator.generateUri(review);

        //then
        Assertions.assertThat(result).isEqualTo("/users/" + user.getId() + "/dashboard");
    }
}