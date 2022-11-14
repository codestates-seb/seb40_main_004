package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Comment;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.ArticleRepository;
import com.morakmorak.morak_back_end.repository.CategoryRepository;
import com.morakmorak.morak_back_end.repository.CommentRepository;
import com.morakmorak.morak_back_end.util.ArticleTestConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    CommentService commentService;
    @Mock
    UserService userService;
    @Mock
    ArticleService articleService;
    @Mock
    CommentRepository commentRepository;

    String VALID_CONTENT = "VALID CONTENT BLAH BLAH";
    Long USERID = 1L;
    Long ARTICLEID =1L;
    @Test
    @DisplayName("존재하지 않는 유저가 등록하려고 할 때 예외")
    void makeComment_v1_failed() {
        //given
        Comment comment = Comment.builder().content(VALID_CONTENT).build();

        given(userService.findVerifiedUserById(any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        //then
        Assertions.assertThatThrownBy(() -> commentService.makeComment_v2(USERID,ARTICLEID,comment))
                .isInstanceOf(BusinessLogicException.class);
    }


}