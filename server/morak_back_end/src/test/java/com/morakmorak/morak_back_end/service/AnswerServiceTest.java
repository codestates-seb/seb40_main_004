package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.AnswerMapper;
import com.morakmorak.morak_back_end.repository.*;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @InjectMocks
    AnswerService answerService;

    @Mock
    AnswerMapper answerMapper;

    @Mock
    UserService userService;

    @Mock
    AnswerRepository answerRepository;

    @Mock
    AnswerLikeRepository answerLikeRepository;

    @Test
    @DisplayName("답변글의 좋아요를 누를때 회원이 좋아요를 처음누르는거면 201코드와 json을 리턴한다.")
    public void pressLikeButton_suc1(){
        //given
        Long answerId = 1L;
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();
        AnswerLike answerLike = AnswerLike.builder().build();

        Answer dbAnswer = Answer.builder().id(1L).answerLike(List.of(answerLike)).build();


        AnswerDto.ResponseAnswerLike responseAnswerLike = AnswerDto.ResponseAnswerLike.builder().userId(1L)
                .answerId(1L).isLiked(true).likeCount(1).build();

        given(answerRepository.findById(anyLong())).willReturn(Optional.of(dbAnswer));
        given(userService.findVerifiedUserById(anyLong())).willReturn(User.builder().id(1L).answerLikes(List.of(answerLike)).build());
        given(answerLikeRepository.checkUserLiked(1L, 1L)).willReturn(Optional.empty());
        given(answerLikeRepository.checkUserLiked(1L, 1L)).willReturn(Optional.of(AnswerLike.builder().build()));
        given(answerMapper.makingResponseAnswerLikeDto(answerId, userInfo.getId(), Boolean.TRUE, 1))
                .willReturn(responseAnswerLike);

        //when
        AnswerDto.ResponseAnswerLike result = answerService.pressLikeButton(answerId, userInfo);
        //then
        assertThat(result.getLikeCount()).isEqualTo(1);
        assertThat(result.getIsLiked()).isTrue();
        assertThat(result.getAnswerId()).isEqualTo(answerId);
        assertThat(result.getUserId()).isEqualTo(userInfo.getId());
    }

    @Test
    @DisplayName("답변글의 좋아요를 누를때 회원이 좋아요를 두번째 누르는거면 좋아요가 취소되고 201코드와 json을 리턴한다.")
    public void pressLikeButton_suc2(){
        //given
        Long answerId = 1L;
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();
        AnswerLike answerLike = AnswerLike.builder().build();

        Answer dbAnswer = Answer.builder().id(1L).build();


        AnswerDto.ResponseAnswerLike responseAnswerLike = AnswerDto.ResponseAnswerLike.builder().userId(1L)
                .answerId(1L).isLiked(false).likeCount(0).build();

        given(answerRepository.findById(anyLong())).willReturn(Optional.of(dbAnswer));
        given(userService.findVerifiedUserById(anyLong())).willReturn(User.builder().id(1L).build());
        given(answerLikeRepository.checkUserLiked(1L, 1L)).willReturn(Optional.empty());
        given(answerLikeRepository.checkUserLiked(1L, 1L)).willReturn(Optional.of(AnswerLike.builder().build()));
        given(answerMapper.makingResponseAnswerLikeDto(answerId, userInfo.getId(), Boolean.TRUE, 0))
                .willReturn(responseAnswerLike);

        //when
        AnswerDto.ResponseAnswerLike result = answerService.pressLikeButton(answerId, userInfo);
        //then
        assertThat(result.getLikeCount()).isEqualTo(0);
        assertThat(result.getIsLiked()).isFalse();
        assertThat(result.getAnswerId()).isEqualTo(answerId);
        assertThat(result.getUserId()).isEqualTo(userInfo.getId());
    }
    @Test
    @DisplayName("답변글의 좋아요를 해당 답변글이 존재하지 않을경우 Article Not Found를 던지고 404 예외코드를 던진다.")
    public void pressLikeButton_fail1(){
        //given
        Long answerId = 1L;
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();

        given(answerRepository.findById(anyLong()))
                .willThrow(new BusinessLogicException(ErrorCode.ARTICLE_NOT_FOUND));

        //when
        //then
        assertThatThrownBy(() -> answerService.pressLikeButton(answerId, userInfo))
                .isInstanceOf(BusinessLogicException.class);

    }

    @Test
    @DisplayName("게시글의 좋아요를 로그인한 회원이 아닐경우 User Not Found를 던지고 404 예외코드를 던진다.")
    public void pressLikeButton_fail2(){
        //given
        Long answerId = 1L;
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();
        Answer dbAnswer = Answer.builder().id(1L).build();

        given(answerRepository.findById(anyLong())).willReturn(Optional.of(dbAnswer));
        given(userService.findVerifiedUserById(anyLong()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> answerService.pressLikeButton(answerId, userInfo))
                .isInstanceOf(BusinessLogicException.class);

    }

}