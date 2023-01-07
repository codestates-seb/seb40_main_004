package com.morakmorak.morak_back_end.controller.user_controller;

import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.enums.*;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.util.ActivityQueryDtoTestImpl;
import com.morakmorak.morak_back_end.util.BadgeQueryDtoTestImpl;
import com.morakmorak.morak_back_end.util.TagQueryDtoTestImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DashboardTest extends UserControllerTest {

    @Test
    @DisplayName("해당 유저 아이디가 존재하지 않을 경우 404 예외 반환")
    void requestDashboard_failed() throws Exception {
        //given
        given(userService.findUserDashboard(any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        //when
        ResultActions perform = mockMvc.perform(get("/users/1/dashboard")
                .header(JWT_HEADER, BEARER_ACCESS_TOKEN));

        //then
        perform.andExpect(status().isNotFound())
                .andDo(document(
                        "대시보드_조회_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("유효하지 않은 엑세스 토큰 (만료, 파기, 서명 등)")
                        )
                ));
    }

    @Test
    @DisplayName("해등 유저 아이디가 존재할 경우 200OK와 유저 대시보드 정보 반환")
    void requestDashboard_success() throws Exception {
        String fileName = "대박사진.jpg";
        String remotePath = "서울역";
        LocalDate now = LocalDate.now();

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

        ReviewDto.Response review2 = ReviewDto.Response.builder()
                .reviewId(ID2)
                .content(CONTENT2)
                .createdAt(NOW_TIME)
                .userInfo(simpleUserDto)
                .build();

        ActivityDto.Response activities1 = ActivityDto.Response.builder()
                .answerCount(1L)
                .commentCount(1L)
                .articleCount(1L)
                .total(3L)
                .createdDate(LocalDate.of(2022,1,1))
                .createdNumber(1)
                .build();

        ActivityDto.Response activities2 = ActivityDto.Response.builder()
                .answerCount(1L)
                .commentCount(1L)
                .articleCount(1L)
                .total(3L)
                .createdDate(LocalDate.of(2022,1,1))
                .createdNumber(1)
                .build();

        ActivityDto.Response activities3 = ActivityDto.Response.builder()
                .answerCount(1L)
                .commentCount(1L)
                .articleCount(1L)
                .total(3L)
                .createdDate(LocalDate.of(2022,1,1))
                .createdNumber(1)
                .build();

        TagQueryDtoTestImpl tag1 = TagQueryDtoTestImpl
                .builder()
                .ranking(1L)
                .tagId(ID1)
                .name(TagName.NODE.toString())
                .build();

        TagQueryDtoTestImpl tag2 = TagQueryDtoTestImpl
                .builder()
                .ranking(2L)
                .tagId(ID2)
                .name(TagName.JAVA.toString())
                .build();

        TagDto.SimpleTag tag3 = TagDto.SimpleTag
                .builder()
                .tagId(ID1)
                .name(TagName.NODE)
                .build();

        TagDto.SimpleTag tag4 = TagDto.SimpleTag
                .builder()
                .tagId(ID1)
                .name(TagName.NODE)
                .build();

        AvatarDto.SimpleResponse avatar = AvatarDto.SimpleResponse.builder()
                .avatarId(ID1)
                .filename(fileName)
                .remotePath(remotePath)
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
                .tags(List.of(tag3, tag4))
                .avatar(avatar)
                .build();

        ArticleDto.ResponseListTypeArticle question2 = ArticleDto.ResponseListTypeArticle.builder()
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
                .tags(List.of(tag3, tag4))
                .avatar(avatar)
                .build();

        BadgeQueryDtoTestImpl badge1 = BadgeQueryDtoTestImpl.builder()
                .badgeId(ID1)
                .name(BadgeName.KINDLY.toString())
                .build();

        BadgeQueryDtoTestImpl badge2 = BadgeQueryDtoTestImpl.builder()
                .badgeId(ID2)
                .name(BadgeName.WISE.toString())
                .build();


        UserDto.ResponseDashBoard answer = UserDto.ResponseDashBoard.builder()
                .userId(ID1)
                .email(EMAIL1)
                .nickname(NICKNAME1)
                .jobType(JobType.DEVELOPER)
                .grade(Grade.VIP)
                .point(THREE)
                .github(GITHUB_URL)
                .blog(TISTORY_URL)
                .rank(2L)
                .infoMessage(CONTENT1)
                .avatar(avatar)
                .tags(List.of(tag1, tag2))
                .reviewBadges(List.of(badge1, badge2))
                .articles(List.of(question1, question2))
                .activities(List.of(activities1, activities2, activities3))
                .reviews(List.of(review1, review2))
                .build();

        given(userService.findUserDashboard(any())).willReturn(answer);

        //when
        ResultActions perform = mockMvc.perform(get("/users/1/dashboard"));

        //then
        perform.andExpect(status().isOk())
                .andDo(document(
                                "대시보드_조회_성공_200",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                responseFields(
                                        fieldWithPath("userId").type(NUMBER).description("유저 db 시퀀스값"),
                                        fieldWithPath("email").type(STRING).description("유저 이메일"),
                                        fieldWithPath("nickname").type(STRING).description("유저 닉네임"),
                                        fieldWithPath("jobType").type(STRING).description("유저 직업"),
                                        fieldWithPath("grade").type(STRING).description("유저 등급"),
                                        fieldWithPath("point").type(NUMBER).description("유저 포인트"),
                                        fieldWithPath("rank").type(NUMBER).description("유저 랭킹"),
                                        fieldWithPath("github").type(STRING).description("깃허브 주소"),
                                        fieldWithPath("blog").type(STRING).description("블로그 주소"),
                                        fieldWithPath("infoMessage").type(STRING).description("자기소개"),
                                        fieldWithPath("avatar").type(OBJECT).description("프로필 사진 및 정보"),
                                        fieldWithPath("avatar.avatarId").type(NUMBER).description("프로필 이미지 db 시퀀스값"),
                                        fieldWithPath("avatar.filename").type(STRING).description("프로필 이미지 파일명"),
                                        fieldWithPath("avatar.remotePath").type(STRING).description("프로필 이미지 url"),
                                        fieldWithPath("tags").type(ARRAY).description("기술 태그 목록"),
                                        fieldWithPath("tags[].tag_Id").type(NUMBER).description("태그 db 시퀀스값"),
                                        fieldWithPath("tags[].tagId").type(NUMBER).description("태그 db 시퀀스값"),
                                        fieldWithPath("tags[].ranking").type(NUMBER).description("태그 db 시퀀스값"),
                                        fieldWithPath("tags[].name").type(STRING).description("태그 이름"),
                                        fieldWithPath("reviewBadges").type(ARRAY).description("리뷰 배지 목록"),
                                        fieldWithPath("reviewBadges[].badge_Id").type(NUMBER).description("리뷰 배지 db 시퀀스값"),
                                        fieldWithPath("reviewBadges[].name").type(STRING).description("배지 이름"),
                                        fieldWithPath("activities").type(ARRAY).description("연간 활동량 목록"),
                                        fieldWithPath("activities[].articleCount").type(NUMBER).description("일일 게시글 작성수"),
                                        fieldWithPath("activities[].answerCount").type(NUMBER).description("일일 답변 작성 수"),
                                        fieldWithPath("activities[].commentCount").type(NUMBER).description("일일 댓글 작성 수"),
                                        fieldWithPath("activities[].total").type(NUMBER).description("일일 토탈 게시글/댓글 작성 수"),
                                        fieldWithPath("activities[].createdDate").type(STRING).description("해당일"),
                                        fieldWithPath("activities[].createdNumber").type(NUMBER).description("해당일을 days of year로 표현"),
                                        fieldWithPath("reviews").type(ARRAY).description("받은 리뷰 목록"),
                                        fieldWithPath("reviews[].reviewId").type(NUMBER).description("리뷰 아이디 db 시퀀스값"),
                                        fieldWithPath("reviews[].content").type(STRING).description("리뷰 내용"),
                                        fieldWithPath("reviews[].userInfo").type(OBJECT).description("리뷰 작성자 정보"),
                                        fieldWithPath("reviews[].userInfo.userId").type(NUMBER).description("리뷰 작성자 db 시퀀스값"),
                                        fieldWithPath("reviews[].userInfo.nickname").type(STRING).description("리뷰 작성자 닉네임"),
                                        fieldWithPath("reviews[].userInfo.grade").type(STRING).description("리뷰 작성자 등급"),
                                        fieldWithPath("reviews[].createdAt").type(STRING).description("리뷰 작성일시"),
                                        fieldWithPath("articles").type(ARRAY).description("질문 목"),
                                        fieldWithPath("articles[].articleId").type(NUMBER).description("질문글 db 시퀀스값"),
                                        fieldWithPath("articles[].category").type(STRING).description("카테고리 (질문글 고정)"),
                                        fieldWithPath("articles[].title").type(STRING).description("질문글 제목"),
                                        fieldWithPath("articles[].clicks").type(NUMBER).description("조회수"),
                                        fieldWithPath("articles[].likes").type(NUMBER).description("좋아요 수"),
                                        fieldWithPath("articles[].isClosed").type(BOOLEAN).description("질문 마감 여부"),
                                        fieldWithPath("articles[].tags").type(ARRAY).description("질문글 태그"),
                                        fieldWithPath("articles[].tags[].tagId").type(NUMBER).description("태그 db 시퀀스값"),
                                        fieldWithPath("articles[].tags[].name").type(STRING).description("태그명"),
                                        fieldWithPath("articles[].commentCount").type(NUMBER).description("댓글 수"),
                                        fieldWithPath("articles[].answerCount").type(NUMBER).description("답글 수"),
                                        fieldWithPath("articles[].createdAt").type(STRING).description("작성일"),
                                        fieldWithPath("articles[].lastModifiedAt").type(STRING).description("수정일"),
                                        fieldWithPath("articles[].userInfo").type(OBJECT).description("작성 유저 정보"),
                                        fieldWithPath("articles[].userInfo.userId").type(NUMBER).description("작성 유저 db 시퀀스값"),
                                        fieldWithPath("articles[].userInfo.nickname").type(STRING).description("작성 유저 닉네임"),
                                        fieldWithPath("articles[].userInfo.grade").type(STRING).description("작성 유저 등급"),
                                        fieldWithPath("articles[].avatar").type(OBJECT).description("유저 프로필 정보"),
                                        fieldWithPath("articles[].avatar.avatarId").type(NUMBER).description("프로필 이미지 db 시퀀스 값"),
                                        fieldWithPath("articles[].avatar.filename").type(STRING).description("프로필 이미지 파일명"),
                                        fieldWithPath("articles[].avatar.remotePath").type(STRING).description("프로필 이미지 경로"),
                                        fieldWithPath("avatar").type(OBJECT).description("유저 프로필 정보"),
                                        fieldWithPath("avatar.avatarId").type(NUMBER).description("프로필 이미지 db 시퀀스 값"),
                                        fieldWithPath("avatar.filename").type(STRING).description("프로필 이미지 파일명"),
                                        fieldWithPath("avatar.remotePath").type(STRING).description("프로필 이미지 경로")
                                )
                        )
                );
    }
}
