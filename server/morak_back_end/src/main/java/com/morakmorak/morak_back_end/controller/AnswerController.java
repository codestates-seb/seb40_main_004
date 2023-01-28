package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.ResponseMultiplePaging;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.File;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.answer_service.AnswerService;
import com.morakmorak.morak_back_end.service.file_service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/articles/{article-id}")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;
    private final FileService fileService;

    @PostMapping("/answers")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseMultiplePaging<AnswerDto.ResponseListTypeAnswer> postAnswer(@PathVariable("article-id") Long articleId,
                                                                               @RequestUser UserDto.UserInfo user,
                                                                               @RequestBody @Valid AnswerDto.RequestPostAnswer request
    ) throws Exception {
        Answer answerNotSaved = Answer.builder()
                .content(request.getContent())
                .build();
        List<File> fileList = fileService.createFileListFrom(request.getFileIdList());
        return answerService.postAnswer(articleId, user.getId(), answerNotSaved, fileList);
    }

    @PatchMapping("/answers/{answer-id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseMultiplePaging<AnswerDto.ResponseListTypeAnswer> updateAnswer(@PathVariable("article-id") Long articleId,
                                                                                 @PathVariable("answer-id") Long answerId,
                                                                                 @RequestUser UserDto.UserInfo user,
                                                                                 @RequestBody @Valid AnswerDto.RequestUpdateAnswer request
    ) throws Exception {
        Answer.AnswerBuilder answer = Answer.builder();
        if ((request.getContent() == null) && (request.getFileIdList().isEmpty())) {
            throw new BusinessLogicException(ErrorCode.BAD_REQUEST);
        } else {
            answer.content(request.getContent());
            List<File> fileList = fileService.createFileListFrom(request.getFileIdList());
            answer.files(fileList);
        }
        Answer answerChanges = answer.build();
        return answerService.editAnswer(articleId, answerId, user.getId(), answerChanges);
    }

    @GetMapping("/answers")
    @ResponseStatus(HttpStatus.OK)
    public ResponseMultiplePaging<AnswerDto.ResponseListTypeAnswer> getAllAnswers(@Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                                                  @Positive @RequestParam(value = "size", defaultValue = "5") int size,
                                                                                  @PathVariable("article-id") Long articleId,
                                                                                @RequestUser UserDto.UserInfo user) {
        Long temp =-1L;
        if (user == null) {
           return answerService.readAllAnswers(articleId, page-1, size);
        } else {
            return answerService.readAllAnswersForUser(articleId, user == null ? temp : user.getId(), page - 1, size);
        }
    }


    @DeleteMapping("/answers/{answer-id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseMultiplePaging<AnswerDto.ResponseListTypeAnswer> deleteAnswer(@PathVariable("article-id") Long articleId,
                                                                                 @PathVariable("answer-id") Long answerId,
                                                                                 @RequestUser UserDto.UserInfo user) {
        return answerService.deleteAnswer(articleId, answerId, user.getId());
    }

    @PostMapping("/answers/{answer-id}/likes")
    public ResponseEntity pressLikeButton(@RequestUser UserDto.UserInfo userInfo,
                                          @PathVariable("article-id") String articleId,
                                          @PathVariable("answer-id") Long answerId) {
        AnswerDto.ResponseAnswerLike responseAnswerLike = answerService.pressLikeButton(answerId, userInfo);

        return new ResponseEntity(responseAnswerLike, HttpStatus.OK);
    }
}
