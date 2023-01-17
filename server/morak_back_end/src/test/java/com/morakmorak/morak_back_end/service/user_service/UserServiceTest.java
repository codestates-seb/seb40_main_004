package com.morakmorak.morak_back_end.service.user_service;

import com.morakmorak.morak_back_end.dto.ActivityDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.*;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.repository.user.UserQueryRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    UserQueryRepository userQueryRepository;

    @InjectMocks
    UserService userService;

    @Mock
    ArticleMapper articleMapper;

    @Mock
    UserMapper userMapper;

    User requestUser;

    @BeforeEach
    public void init() {
        requestUser = User.builder()
                .nickname(NICKNAME1)
                .infoMessage(CONTENT1)
                .github(GITHUB_URL)
                .blog(TISTORY_URL)
                .jobType(JobType.DEVELOPER)
                .build();
    }

    @Test
    @DisplayName("상세 활동 내역을 조회하려는 유저를 찾을 수 없으면 BusinessLogicException이 발생한다")
    void findActivity_failed() {
        //given
        LocalDate date = LocalDate.parse("2023-01-01");

        //when //then
        assertThatThrownBy(() -> userService.findActivityHistoryOn(date, ID1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("조회 범위가 올해가 아닐 경우 예외 발생(1)")
    void findActivity_failed2() {
        //given
        LocalDate date = LocalDate.parse("2002-01-01");

        //when //then
        assertThatThrownBy(() -> userService.findActivityHistoryOn(date, ID1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("조회 범위가 올해가 아닐 경우 예외 발생(2)")
    void findActivity_failed3() {
        //given
        LocalDate date = LocalDate.parse("2032-01-01");

        //when //then
        assertThatThrownBy(() -> userService.findActivityHistoryOn(date, ID1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("해당 날짜에 아무런 데이터가 없더라도 예외가 발생하지 않는다.")
    void findActivity_success() {
        //given
        LocalDate date = LocalDate.parse("2023-01-01");

        given(userRepository.findById(ID1)).willReturn(Optional.of(User.builder().build()));
        given(userQueryRepository.getWrittenCommentHistoryOn(date, ID1)).willReturn(List.of());
        given(userQueryRepository.getWrittenArticleHistoryOn(date,ID1)).willReturn(List.of());
        given(userQueryRepository.getWrittenAnswerHistoryOn(date ,ID1)).willReturn(List.of());

        //when
        ActivityDto.Detail result = userService.findActivityHistoryOn(date, ID1);

        //then
        assertThat(result.getAnswers()).isEmpty();
        assertThat(result.getComments()).isEmpty();
        assertThat(result.getArticles()).isEmpty();
        assertThat(result.getTotal()).isEqualTo(0);
    }

    @Test
    @DisplayName("반환 값을 조합하여 ActivityDto.Detail 객체를 반환한다")
    void findActivity_success2() {
        //given
        LocalDate date = LocalDate.parse("2023-01-01");

        ActivityDto.Article article = ActivityDto.Article.builder()
                .articleId(1L)
                .title(CONTENT1)
                .createdDate(LocalDate.now())
                .commentCount(10L)
                .likeCount(10L)
                .build();

        ActivityDto.Comment comment = ActivityDto.Comment.builder()
                .articleId(1L)
                .content(CONTENT2)
                .build();


        given(userRepository.findById(ID1)).willReturn(Optional.of(User.builder().build()));
        given(userQueryRepository.getWrittenCommentHistoryOn(date, ID1)).willReturn(List.of(comment));
        given(userQueryRepository.getWrittenArticleHistoryOn(date,ID1)).willReturn(List.of(article));
        given(userQueryRepository.getWrittenAnswerHistoryOn(date ,ID1)).willReturn(List.of(article));

        //when
        ActivityDto.Detail result = userService.findActivityHistoryOn(date, ID1);

        //then
        assertThat(result.getAnswers().get(0).getTitle()).isEqualTo(CONTENT1);
        assertThat(result.getComments().get(0).getArticleId()).isEqualTo(1L);
        assertThat(result.getArticles().get(0).getCommentCount()).isEqualTo(10L);
        assertThat(result.getArticles().get(0).getTitle()).isEqualTo(CONTENT1);
        assertThat(result.getArticles().get(0).getLikeCount()).isEqualTo(10L);
        assertThat(result.getTotal()).isEqualTo(3);
    }
}
