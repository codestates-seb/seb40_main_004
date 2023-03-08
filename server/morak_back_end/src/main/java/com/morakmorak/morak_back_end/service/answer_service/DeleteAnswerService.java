package com.morakmorak.morak_back_end.service.answer_service;

import com.amazonaws.services.s3.AmazonS3;
import com.morakmorak.morak_back_end.domain.PointCalculator;
import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.ResponseMultiplePaging;
import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.answer.AnswerRepository;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.service.ArticleService;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import lombok.AllArgsConstructor;

import java.net.URI;

@AllArgsConstructor
public class DeleteAnswerService {
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final AnswerService answerService;
    private final ArticleRepository articleRepository;
    private final VerifyAnswerService verifyAnswerService;
    private final UserService userService;
    private final ArticleService articleService;
    private final PointCalculator pointCalculator;
    private final AmazonS3 amazonS3;

    public ResponseMultiplePaging<AnswerDto.ResponseListTypeAnswer> deleteAnswer(Long articleId, Long answerId, Long userId) {

        Answer verifiedAnswer = verifyAnswerService.findVerifiedAnswer(answerId);

        User verifiedUser = userService.findVerifiedUserById(userId);
        Article verifiedArticle = articleService.findVerifiedArticle(articleId);

        verifyAnswerService.checkUserPermission(verifiedAnswer, verifiedUser);
        verifyAnswerService.checkArticleStatusPosting(verifiedArticle);
        verifyAnswerService.checkAnswerIsPicked(verifiedAnswer);

        answerRepository.deleteById(answerId);

        verifiedUser.minusPoint(verifiedAnswer, pointCalculator);

        int page=5;
        int size = 5;
        return answerService.readAllAnswersForUser(articleId, userId, page, size);

    }
}
