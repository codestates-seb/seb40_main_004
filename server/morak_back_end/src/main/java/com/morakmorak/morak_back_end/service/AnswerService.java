package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.domain.NotificationGenerator;
import com.morakmorak.morak_back_end.domain.PointCalculator;
import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.ResponseMultiplePaging;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.AnswerMapper;
import com.morakmorak.morak_back_end.repository.BookmarkRepository;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.answer.AnswerLikeRepository;
import com.morakmorak.morak_back_end.repository.answer.AnswerQueryRepository;
import com.morakmorak.morak_back_end.repository.answer.AnswerRepository;
import com.morakmorak.morak_back_end.repository.notification.NotificationRepository;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AnswerService {
    private final ArticleService articleService;
    private final UserService userService;
    private final AnswerRepository answerRepository;
    private final AnswerQueryRepository answerQueryRepository;
    private final BookmarkRepository bookmarkRepository;
    private final AnswerLikeRepository answerLikeRepository;
    private final AnswerMapper answerMapper;
    private final NotificationRepository notificationRepository;
    private final PointCalculator pointCalculator;
    private final FileRepository fileRepository;

    int page=0;
    int size=5;

    public ResponseMultiplePaging<AnswerDto.ResponseListTypeAnswer> postAnswer(Long articleId, Long userId, Answer answerNotSaved, List<File> fileList) throws Exception {
        User verifiedUser = userService.findVerifiedUserById(userId);
        Article verifiedArticle = articleService.findVerifiedArticle(articleId);

        checkArticleStatusPosting(verifiedArticle);
        checkArticleIsQna(verifiedArticle);
        checkArticleIsOpen(verifiedArticle);

        answerNotSaved.injectUser(verifiedUser);
        answerRepository.save(answerNotSaved);

        answerNotSaved.injectArticle(verifiedArticle);
        answerRepository.save(answerNotSaved);

        fileList.stream().forEach(file -> file.attachToAnswer(answerNotSaved));
        Answer savedAnswer = answerRepository.save(answerNotSaved);

        NotificationGenerator generator = NotificationGenerator.of(verifiedUser, savedAnswer);
        Notification notification = generator.generateNotification();

        verifiedUser.addPoint(savedAnswer, pointCalculator);
        notificationRepository.save(notification);

        return readAllAnswersForUser(articleId, userId, page, size);
    }
    private void checkArticleStatusPosting(Article verifiedArticle) {
        if (!verifiedArticle.statusIsPosting()) { throw new BusinessLogicException(ErrorCode.NO_ACCESS_TO_THAT_OBJECT);}
    }

    private void checkArticleIsQna(Article verifiedArticle) {
        if (!verifiedArticle.isQuestion()) {throw new BusinessLogicException(ErrorCode.INVALID_CATEGORY);}}

    private void checkArticleIsOpen(Article verifiedArticle) {
        if (verifiedArticle.isClosedArticle()) {throw new BusinessLogicException(ErrorCode.CLOSED_ANSWER);}}

    public Answer findVerifiedAnswerById(Long answerId) {
        return answerRepository.findById(answerId).orElseThrow(() -> new BusinessLogicException(ErrorCode.ANSWER_NOT_FOUND));}

    public ResponseMultiplePaging<AnswerDto.ResponseListTypeAnswer> editAnswer(Long articleId, Long answerId, Long userId, Answer answerChanges) {

        Answer verifiedAnswer = findVerifiedAnswerById(answerId);
        User verifiedUser = userService.findVerifiedUserById(userId);
        Article verifiedArticle = articleService.findVerifiedArticle(articleId);

        checkUserPermission(verifiedAnswer, verifiedUser);

        checkArticleStatusPosting(verifiedArticle);

        checkAnswerIsPicked(verifiedAnswer);

        attachFilesToAnswer(verifiedAnswer, answerChanges.getFiles());
        verifiedAnswer.updateAnswer(answerChanges);

        return readAllAnswersForUser(articleId, userId, page, size);
    }

    private void checkAnswerIsPicked(Answer verifiedAnswer) {
        if (verifiedAnswer.isPickedAnswer()) {throw new BusinessLogicException(ErrorCode.UNABLE_TO_CHANGE_ANSWER);}}

    private void checkUserPermission(Answer answer, User requestUser) {
        if (!answer.hasPermissionWith(requestUser)) {throw new BusinessLogicException(ErrorCode.NO_ACCESS_TO_THAT_OBJECT);}}



    public ResponseMultiplePaging<AnswerDto.ResponseListTypeAnswer> readAllAnswers(Long articleId, int page, int size) {
        Page<Answer> answersInPage = answerQueryRepository.findAllByArticleId_PickedFirst(articleId, PageRequest.of(page, size));
        List<AnswerDto.ResponseListTypeAnswer> answers = answersInPage.getContent().stream()
                        .map(AnswerDto.ResponseListTypeAnswer::of).collect(Collectors.toList());

        return new ResponseMultiplePaging<>(answers, answersInPage);
    }



    public ResponseMultiplePaging<AnswerDto.ResponseListTypeAnswer> deleteAnswer(Long articleId, Long answerId, Long userId) {

        Answer verifiedAnswer = findVerifiedAnswerById(answerId);
        User verifiedUser = userService.findVerifiedUserById(userId);
        Article verifiedArticle = articleService.findVerifiedArticle(articleId);

        checkUserPermission(verifiedAnswer, verifiedUser);

        checkArticleStatusPosting(verifiedArticle);

        checkAnswerIsPicked(verifiedAnswer);

        answerRepository.deleteById(answerId);

        verifiedUser.minusPoint(verifiedAnswer, pointCalculator);

        return readAllAnswersForUser(articleId, userId, page, size);

    }



    public AnswerDto.ResponseAnswerLike pressLikeButton(Long answerId, UserDto.UserInfo userInfo) {

        Optional.ofNullable(userInfo).orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        Answer dbAnswer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.ANSWER_NOT_FOUND));

        User dbUser = userService.findVerifiedUserById(userInfo.getId());

        answerLikeRepository.checkUserLiked(dbUser.getId(), dbAnswer.getId()).ifPresentOrElse(
                answerLike -> {
                    dbUser.minusPoint(answerLike, pointCalculator);
                    answerLikeRepository.deleteById(answerLike.getId());
                },
                () -> {
                    AnswerLike answerLike = AnswerLike.builder().answer(dbAnswer).user(dbUser).build();
                    dbAnswer.getAnswerLike().add(answerLike);
                    dbUser.getAnswerLikes().add(answerLike);
                    dbUser.addPoint(answerLike, pointCalculator);

                    if (dbAnswer.getAnswerLike().size() % 10 == 0) {
                        NotificationGenerator generator = NotificationGenerator.of(answerLike, dbAnswer.getAnswerLike().size());
                        Notification notification = generator.generateNotification();
                        notificationRepository.save(notification);
                    }
                }
        );

        Boolean isLiked = answerLikeRepository
                .checkUserLiked(dbUser.getId(), dbAnswer.getId()).isPresent();

        Integer likeCount = dbAnswer.getAnswerLike().size();

        return answerMapper.makingResponseAnswerLikeDto(dbAnswer.getId(), dbUser.getId(), isLiked, likeCount);
    }


    public void attachFilesToAnswer(Answer answer, List<File> fileList) {
        fileList.stream().forEach(file -> {
            file.attachToAnswer(answer);
        });
    }

    public ResponseMultiplePaging<AnswerDto.ResponseListTypeAnswer> readAllAnswersForUser(Long articleId, Long userId, int page, int size) {
        if (userId == -1L) { return readAllAnswers(articleId, page, size); }

        userService.findVerifiedUserById(userId);
        articleService.findVerifiedArticle(articleId);

        Page<Answer> answersInPage = answerQueryRepository.findAllByArticleId_PickedFirst(articleId, PageRequest.of(page, size));

        List<AnswerDto.ResponseListTypeAnswer> answers = answersInPage.getContent().stream()
                .map(answer -> {
                            Boolean isPicked = answer.getIsPicked();
                            Boolean isLiked = answerLikeRepository.checkUserLiked(userId, answer.getId()).isPresent();
                            Integer answerLikeCount = answer.getAnswerLike().size();
                            CommentDto.Response commentPreview = CommentDto.Response.previewOfAnswer(answer.getComments());
                            Integer commentCount = answer.getComments().size();
                            return answerMapper.answerToResponseListTypeAnswer(answer, isPicked, isLiked, answerLikeCount, commentPreview, commentCount);
                        }
                ).collect(Collectors.toList());

        return new ResponseMultiplePaging<>(answers, answersInPage);
    }
}
