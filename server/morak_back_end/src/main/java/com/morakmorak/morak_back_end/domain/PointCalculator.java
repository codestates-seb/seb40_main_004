package com.morakmorak.morak_back_end.domain;

import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import org.springframework.stereotype.Component;

@Component
public class PointCalculator {
    final Integer ARTICLE_POINT = 50;
    final Integer QUESTION_POINT = 20;
    final Integer ANSWER_POINT = 50;
    final Integer COMMENT_POINT = 10;
    final Integer LIKE_POINT = 1;

    public Integer calculatePaymentPoint(Object obj) {
        if (obj.getClass().equals(Answer.class)) return ANSWER_POINT;
        if (obj.getClass().equals(Comment.class)) return COMMENT_POINT;
        if (obj.getClass().equals(AnswerLike.class)) return LIKE_POINT;
        if (obj.getClass().equals(ArticleLike.class)) return LIKE_POINT;
        if (obj.getClass().equals(Article.class)) {
            if (((Article) obj).getCategory().getName().equals(CategoryName.INFO)) return ARTICLE_POINT;
            if (((Article) obj).getCategory().getName().equals(CategoryName.QNA)) return QUESTION_POINT;
        }

        throw new IllegalArgumentException("유효하지 않은 객체 타입");
    }
}
