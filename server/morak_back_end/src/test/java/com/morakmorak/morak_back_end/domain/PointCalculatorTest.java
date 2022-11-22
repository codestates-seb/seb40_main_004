package com.morakmorak.morak_back_end.domain;

import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.parameters.P;

import static org.junit.jupiter.api.Assertions.*;

class PointCalculatorTest {
    PointCalculator pointCalculator = new PointCalculator();

    @Test
    void calculatePaymentPoint1() {
        //given
        Article article = Article.builder().category(new Category(CategoryName.INFO)).build();
        //when
        Integer result = pointCalculator.calculatePaymentPoint(article);
        //then
        Assertions.assertThat(result).isEqualTo(pointCalculator.ARTICLE_POINT);
    }

    @Test
    void calculatePaymentPoint2() {
        //given
        Article article = Article.builder().category(new Category(CategoryName.QNA)).build();
        //when
        Integer result = pointCalculator.calculatePaymentPoint(article);
        //then
        Assertions.assertThat(result).isEqualTo(pointCalculator.QUESTION_POINT);
    }

    @Test
    void calculatePaymentPoint3() {
        //given
        Article article = Article.builder().category(new Category(CategoryName.INFO)).build();
        //when
        Integer result = pointCalculator.calculatePaymentPoint(article);
        //then
        Assertions.assertThat(result).isEqualTo(pointCalculator.ARTICLE_POINT);
    }

    @Test
    void calculatePaymentPoint4() {
        //given
        Answer answer = Answer.builder().build();
        //when
        Integer result = pointCalculator.calculatePaymentPoint(answer);
        //then
        Assertions.assertThat(result).isEqualTo(pointCalculator.ANSWER_POINT);
    }

    @Test
    void calculatePaymentPoint5() {
        //given
        ArticleLike articleLike = ArticleLike.builder().build();
        //when
        Integer result = pointCalculator.calculatePaymentPoint(articleLike);
        //then
        Assertions.assertThat(result).isEqualTo(pointCalculator.LIKE_POINT);
    }

    @Test
    void calculatePaymentPoint6() {
        //given
        AnswerLike answerLike = AnswerLike.builder().build();
        //when
        Integer result = pointCalculator.calculatePaymentPoint(answerLike);
        //then
        Assertions.assertThat(result).isEqualTo(pointCalculator.LIKE_POINT);
    }

    @Test
    void calculatePaymentPoint7() {
        //given
        Comment comment = Comment.builder().build();
        //when
        Integer result = pointCalculator.calculatePaymentPoint(comment);
        //then
        Assertions.assertThat(result).isEqualTo(pointCalculator.COMMENT_POINT);
    }

    @Test
    void calculatePaymentPoint8() {
        //given
        Avatar avatar = Avatar.builder().build();
        //when then
        Assertions.assertThatThrownBy(() -> pointCalculator.calculatePaymentPoint(avatar))
                .isInstanceOf(IllegalArgumentException.class);
    }
}