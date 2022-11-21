package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.AnswerMapper;
import com.morakmorak.morak_back_end.repository.answer.AnswerLikeRepository;
import com.morakmorak.morak_back_end.repository.answer.AnswerRepository;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AnswerService {
    private final ArticleService articleService;
    private final UserService userService;
    private final CommentService commentService;
    private final AnswerRepository answerRepository;
    private final AnswerLikeRepository answerLikeRepository;
    private final AnswerMapper answerMapper;

    public AnswerDto.SimpleResponsePostAnswer postAnswer(Long articleId, Long userId, Answer answerNotSaved, List<File> fileList) throws Exception {
        User verifiedUser = userService.findVerifiedUserById(userId);
        Article verifiedArticle = articleService.findVerifiedArticle(articleId);

        if (verifiedArticle.isQuestion() && !verifiedArticle.isClosedArticle()
                && commentService.isEditableStatus(verifiedArticle)) {
            Answer savedAnswer = answerRepository.save(injectAllInto(answerNotSaved, verifiedUser, verifiedArticle, fileList));
            return AnswerDto.SimpleResponsePostAnswer.of(savedAnswer);
        } else {
            throw new BusinessLogicException(ErrorCode.UNABLE_TO_ANSWER);
        }
    }

    private Answer injectAllInto(Answer answerNotSaved, User verifiedUser, Article verifiedArticle, List<File> fileList) {
        attachFilesToAnswer(answerNotSaved, fileList);
        answerNotSaved.injectUser(verifiedUser).injectArticle(verifiedArticle);
        return answerNotSaved;
    }

    public void attachFilesToAnswer(Answer answer, List<File> fileList) {
        fileList.stream().forEach(file -> {
            file.attachToAnswer(answer);
        });
    }
    public AnswerDto.ResponseAnswerLike pressLikeButton(Long answerId, UserDto.UserInfo userInfo) {

        Optional.ofNullable(userInfo).orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        Answer dbAnswer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.ANSWER_NOT_FOUND));

        User dbUser = userService.findVerifiedUserById(userInfo.getId());

        answerLikeRepository.checkUserLiked(dbUser.getId(), dbAnswer.getId()).ifPresentOrElse(
                answerLike -> {
                    answerLikeRepository.deleteById(answerLike.getId());
                },
                () -> {
                    AnswerLike answerLike = AnswerLike.builder().answer(dbAnswer).user(dbUser).build();
                    dbAnswer.getAnswerLike().add(answerLike);
                    dbUser.getAnswerLikes().add(answerLike);
                }
        );

        Boolean isLiked = answerLikeRepository
                .checkUserLiked(dbUser.getId(), dbAnswer.getId()).isPresent();

        Integer likeCount = dbUser.getAnswerLikes().size();

        return answerMapper.makingResponseAnswerLikeDto(dbAnswer.getId(), dbUser.getId(), isLiked, likeCount);
    }
}
