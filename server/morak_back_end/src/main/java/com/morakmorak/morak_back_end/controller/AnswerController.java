package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.File;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.AnswerService;
import com.morakmorak.morak_back_end.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/articles/{article-id}")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;
    private final FileService fileService;

    @PostMapping("/answers")
    @ResponseStatus(HttpStatus.CREATED)
    public AnswerDto.SimpleResponsePostAnswer postAnswer(@PathVariable("article-id") Long articleId,
                                                         @RequestUser UserDto.UserInfo user,
                                                         @RequestBody @Valid AnswerDto.RequestPostAnswer request
    ) throws Exception {
        Answer answerNotSaved = Answer.builder()
                .content(request.getContent())
                .build();
        List<File> fileList = fileService.createFileListFrom(request.getFileIdList());
        return answerService.postAnswer(articleId, user.getId(), answerNotSaved, fileList);
    }


}
