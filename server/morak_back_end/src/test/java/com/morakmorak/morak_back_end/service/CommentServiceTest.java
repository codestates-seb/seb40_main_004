package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.domain.PointCalculator;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Comment;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.CommentRepository;
import com.morakmorak.morak_back_end.repository.notification.NotificationRepository;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import com.morakmorak.morak_back_end.util.TestConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    @Mock
    PointCalculator pointCalculator;
    @Mock
    NotificationRepository notificationRepository;

    String VALID_CONTENT = "VALID CONTENT BLAH BLAH";
    Long USERID = 1L;
    Long ARTICLEID =1L;
    @Test
    @DisplayName("존재하지 않는 유저가 등록하려고 할 때 예외")
    void makeComment_failed_1() {
        //given
        Comment comment = Comment.builder().content(VALID_CONTENT).build();

        given(userService.findVerifiedUserById(any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        //then
        Assertions.assertThatThrownBy(() -> commentService.makeComment(USERID,ARTICLEID,comment))
                .isInstanceOf(BusinessLogicException.class);
    }
    @Test
    @DisplayName("올바른 댓글 등록요청일 시 commentRepository의 save가 실행되며, 결과 comment의 id가 ")
    void makeComment_success_1() {
        //given
        User user = User.builder().id(1L).nickname(NICKNAME1).build();
        Comment comment = Comment.builder().content(VALID_CONTENT).user(user).build();
        Article article = Article.builder().title(CONTENT1).id(1L).user(user).build();

        given(userService.findVerifiedUserById(any())).willReturn(user);
        given(articleService.findVerifiedArticle(any())).willReturn(article);
        given(commentRepository.save(comment)).willReturn(comment);

        //when
        CommentDto.Response target = commentService.makeComment(1L, 1L, comment);

        //then
        verify(commentRepository, times(1)).save(comment);
    }
}