package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Comment;
import com.morakmorak.morak_back_end.repository.CommentRepository;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.ArticleService;
import com.morakmorak.morak_back_end.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/articles/{article-id}")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto.Reponse requestPostComment(@RequestBody @Valid CommentDto.RequestPost request,
                                      @PathVariable("article-id") Long articleId
                                      , @RequestUser UserDto.UserInfo user) {


//        Comment madeComment_v1 = commentService.makeComment_withoutEntity(user.getId(), articleId,
//                request.getContent());
//        Comment madeComment_v1 = Comment.builder().content(request.getContent()).build();
        Comment commentNotSaved = Comment.builder().content(request.getContent()).build();
        CommentDto.Reponse madeComment_v2 = commentService.makeComment_v2(user.getId(), articleId, commentNotSaved);

//        return madeComment_v1;
        return madeComment_v2;



        //유저를 받아오고 그담에 유저롤을 저장해야 되는데
        //롤을 저장하고 유저롤을 저장해도 된다는 건가
        //내가 생각한 건 코멘트를 찾아온 다음에 코멘트에 저장하는 거였는데
        //그러려면 코멘트가 초기화되어있으면 안되는거아닌가 생각했음
        //코멘트를 냅다 저장하고 나서 코멘트에 포함된
        //아니 근데 코멘트가 빌드가 안되어있어야 어디 저장할 구멍이 생기는 거 아닌가


    }
}
