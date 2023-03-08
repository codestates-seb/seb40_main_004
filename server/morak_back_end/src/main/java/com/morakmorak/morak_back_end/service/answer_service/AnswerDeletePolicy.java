package com.morakmorak.morak_back_end.service.answer_service;

import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.User;

public interface AnswerDeletePolicy {
    Answer findVerifiedAnswer(Long answerId);
    void checkUserPermission(Answer answer, User requestUser);
    void checkArticleStatusPosting(Article verifiedArticle);
    void checkAnswerIsPicked(Answer verifiedAnswer);

}
