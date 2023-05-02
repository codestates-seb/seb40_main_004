package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.ResponseMultiplePaging;
import com.morakmorak.morak_back_end.dto.ResponsePagesWithLinks;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.File;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.answer_service.AnswerService;
import com.morakmorak.morak_back_end.service.file_service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/articles/{article-id}")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;
    private final FileService fileService;

    @PostMapping("/answers")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponsePagesWithLinks<AnswerDto.ResponseListTypeAnswer> postAnswer(@PathVariable("article-id") Long articleId,
                                                                               @RequestUser UserDto.UserInfo user,
                                                                               @RequestBody @Valid AnswerDto.RequestPostAnswer request
    ) throws Exception {
        Answer answerNotSaved = Answer.builder()
                .content(request.getContent())
                .build();
        List<File> fileList = fileService.createFileListFrom(request.getFileIdList());
        ResponseMultiplePaging<AnswerDto.ResponseListTypeAnswer> pagedData = answerService.postAnswer(articleId, user.getId(), answerNotSaved, fileList);
        ResponsePagesWithLinks<AnswerDto.ResponseListTypeAnswer> response = ResponsePagesWithLinks.of(pagedData);

        Link selfLink = linkTo(methodOn(AnswerController.class).postAnswer(articleId, user, request)).withSelfRel();
        response.add(selfLink);
        /*가장 마지막 페이지의 답변을 보여주는 링크*/
        /*수정 링크*/
        /*삭제 링크*/
        return response;
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
    @ResponseStatus(HttpStatus.OK)
    public AnswerDto.ResponseAnswerLike pressLikeButton(@RequestUser UserDto.UserInfo userInfo,
                                                        @PathVariable("article-id") String articleId,
                                                        @PathVariable("answer-id") Long answerId) {
        return answerService.pressLikeButton(answerId, userInfo);
    }
}
