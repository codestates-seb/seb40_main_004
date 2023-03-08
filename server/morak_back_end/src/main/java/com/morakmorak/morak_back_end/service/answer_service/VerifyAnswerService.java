package com.morakmorak.morak_back_end.service.answer_service;

import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.answer.AnswerRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
@AllArgsConstructor
public class VerifyAnswerService implements AnswerDeletePolicy{
    private final AnswerDeletePolicy answerDeletePolicy;
    private AnswerRepository answerRepository;

    public Answer findVerifiedAnswer(Long answerId) {
        return answerRepository.findById(answerId).orElseThrow(() -> new BusinessLogicException(ErrorCode.ANSWER_NOT_FOUND));
    }
    public void checkUserPermission(Answer answer, User requestUser) {
        if (!answer.hasPermissionWith(requestUser)) {throw new BusinessLogicException(ErrorCode.NO_ACCESS_TO_THAT_OBJECT);}}
    public void checkArticleStatusPosting(Article verifiedArticle) {
        if (!verifiedArticle.statusIsPosting()) { throw new BusinessLogicException(ErrorCode.NO_ACCESS_TO_THAT_OBJECT);}
    }

     public void checkAnswerIsPicked(Answer verifiedAnswer) {
        if (verifiedAnswer.isPickedAnswer()) {throw new BusinessLogicException(ErrorCode.UNABLE_TO_CHANGE_ANSWER);}}

}
