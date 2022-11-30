package com.morakmorak.morak_back_end.service.user_service;

import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.enums.*;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.util.ActivityQueryDtoTestImpl;
import com.morakmorak.morak_back_end.util.BadgeQueryDtoTestImpl;
import com.morakmorak.morak_back_end.util.TagQueryDtoTestImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ONE;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.CONTENT1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

public class getUserDashboardTest extends UserServiceTest {
    @Test
    @DisplayName("getUSerDashboard에 실패하면 나머지 로직은 실행되지 않고 예외가 발생한다.")
    void findUserDashboard_failed() {
        //given
        given(userQueryRepository.getUserDashboardBasicInfo(ID1)).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        //when then
        assertThatThrownBy(() -> userService.findUserDashboard(ID1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("")
    void findUserDashboard_success() {
        //given
        UserDto.ResponseSimpleUserDto simpleUserDto = UserDto.ResponseSimpleUserDto.builder()
                .userId(ID1)
                .grade(Grade.VIP)
                .nickname(NICKNAME1)
                .build();

        ReviewDto.Response review1 = ReviewDto.Response.builder()
                .reviewId(ID1)
                .content(CONTENT1)
                .createdAt(NOW_TIME)
                .userInfo(simpleUserDto)
                .build();

        ActivityDto.Response activities1 = ActivityDto.Response.builder()
                .answerCount(1L)
                .commentCount(1L)
                .articleCount(1L)
                .total(3L)
                .createdDate(LocalDate.of(2022,1,1))
                .build();

        TagQueryDtoTestImpl tag1 = TagQueryDtoTestImpl
                .builder()
                .tagId(ID1)
                .name(TagName.NODE.getName())
                .build();

        TagDto.SimpleTag tag2 = TagDto.SimpleTag
                .builder()
                .tagId(ID1)
                .name(TagName.NODE)
                .build();

        ArticleDto.ResponseListTypeArticle question1 = ArticleDto.ResponseListTypeArticle.builder()
                .articleId(ID1)
                .answerCount(TWO)
                .category(CategoryName.QNA)
                .clicks(ONE)
                .likes(TWO)
                .isClosed(Boolean.FALSE)
                .title(TITLE1)
                .commentCount(ONE)
                .createdAt(NOW_TIME)
                .lastModifiedAt(NOW_TIME)
                .userInfo(simpleUserDto)
                .tags(List.of(tag2))
                .build();

        BadgeQueryDtoTestImpl badge1 = BadgeQueryDtoTestImpl.builder()
                .badgeId(ID1)
                .name(BadgeName.KINDLY.getName())
                .build();

        AvatarDto.SimpleResponse avatar = AvatarDto.SimpleResponse.builder()
                .avatarId(ID1)
                .filename("fileName")
                .remotePath("remotePath")
                .build();

        UserDto.ResponseDashBoard dashBoard = UserDto.ResponseDashBoard.builder()
                .userId(ID1)
                .email(EMAIL1)
                .nickname(NICKNAME1)
                .jobType(JobType.DEVELOPER)
                .grade(Grade.VIP)
                .point(THREE)
                .github(GITHUB_URL)
                .blog(TISTORY_URL)
                .avatar(avatar)
                .tags(List.of(tag1))
                .reviewBadges(List.of(badge1))
                .articles(List.of(question1))
                .activities(List.of(activities1))
                .reviews(List.of(review1))
                .build();

        LocalDate january1st = LocalDate.now().withDayOfYear(1);
        LocalDate december31st = LocalDate.now().withDayOfYear(365);
        ActivityDto.Temporary activityDto = ActivityDto.Temporary.builder().count(1L).build();


        given(userQueryRepository.getUserDashboardBasicInfo(ID1)).willReturn(dashBoard);
        given(userQueryRepository.getUserDashboardBasicInfo(ID1)).willReturn(UserDto.ResponseDashBoard.builder().email(EMAIL1).build());
        given(userQueryRepository.get50RecentQuestions(ID1)).willReturn(List.of(Article.builder().id(ID1).build()));
        given(userQueryRepository.getReceivedReviews(ID1)).willReturn(List.of(review1));
        given(userRepository.getUsersTop3Badges(ID1)).willReturn(List.of(badge1));
        given(userRepository.getUsersTop3Tags(ID1)).willReturn(List.of(tag1));
        given(userQueryRepository.getUserArticlesDataBetween(january1st, december31st, ID1)).willReturn(List.of(activityDto));
        given(userQueryRepository.getUserAnswersDataBetween(january1st, december31st, ID1)).willReturn(List.of(activityDto));
        given(userQueryRepository.getUserCommentsDataBetween(january1st, december31st, ID1)).willReturn(List.of(activityDto));
        given(articleMapper.articleToResponseSearchResultArticle(any(Article.class), anyInt(), anyInt(), any(List.class), anyInt())).willReturn(question1);

        //when
        UserDto.ResponseDashBoard result = userService.findUserDashboard(ID1);

        // then
        assertThat(result.getActivities().get(0).getAnswerCount()).isEqualTo(ONE);
        assertThat(result.getArticles().get(0).getCommentCount()).isEqualTo(ONE);
        assertThat(result.getEmail()).isEqualTo(EMAIL1);
        assertThat(result.getReviews().get(0).getContent()).isEqualTo(CONTENT1);
    }
}
