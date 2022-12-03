package com.morakmorak.morak_back_end.controller.user_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.controller.UserController;
import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.JobType;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.exception.ErrorCode.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WithMockUser
@Import(SecurityTestConfig.class)
@WebMvcTest({UserController.class, ExceptionController.class, UserMapper.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;

    JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    public void init() {
        jwtTokenUtil = new JwtTokenUtil(SECRET_KEY, REFRESH_KEY);
    }

    @Test
    @DisplayName("조회하려는 유저의 정보를 찾을 수 없을 경우 404 NotFound를 반환한다.")
    public void getUserActivity_success() throws Exception {
        // given
        String stringDate = "2022-01-01";
        LocalDate date = LocalDate.parse(stringDate);
        given(userService.findActivityHistoryOn(any(LocalDate.class), any())).willThrow(new BusinessLogicException(USER_NOT_FOUND));

        // when
        ResultActions perform = mockMvc.perform(get("/users/{user-id}/profiles/dashboard/activities/{date}", 1, "2022-01-01"));

        // then
        perform.andExpect(status().isNotFound())
                .andDo(
                        document("대시보드_상세조회_실패_404",
                                getDocumentRequest(),
                                getDocumentResponse()
                        )
                );
    }

    @Test
    @DisplayName("조회하려는 날짜 포맷이 올바르지 않을 경우 400 코드를 반환한다.")
    public void getUserActivity_failed2() throws Exception {
        // given
        String stringDate = "2022-01-01";
        LocalDate date = LocalDate.parse(stringDate);

        // when
        ResultActions perform = mockMvc.perform(get("/users/{user-id}/profiles/dashboard/activities/{date}", 1, "2022-01-101"));

        // then
        perform.andExpect(status().isNotFound())
                .andDo(
                        document("대시보드_상세조회_실패_400",
                                getDocumentRequest(),
                                getDocumentResponse()
                        )
                );
    }

    @Test
    @DisplayName("조회하려는 날짜 범위가 올해가 아닐 경우 416 코드를 반환한다.")
    public void getUserActivity_failed3() throws Exception {
        // given
        String stringDate = "2002-01-01";
        LocalDate date = LocalDate.parse(stringDate);
        given(userService.findActivityHistoryOn(date, 1L)).willThrow(new BusinessLogicException(INVALID_DATE_RANGE));

        // when
        ResultActions perform = mockMvc.perform(get("/users/{user-id}/dashboard/activities/{date}", 1, "2002-01-01"));

        // then
        perform.andExpect(status().isRequestedRangeNotSatisfiable())
                .andDo(
                        document("대시보드_상세조회_실패_416",
                                getDocumentRequest(),
                                getDocumentResponse()
                        )
                );
    }

    @Test
    @DisplayName("조회하려는 유저의 정보를 찾으면 해당 날짜의 활동량을 반환한다.")
    public void getUserActivity_failed() throws Exception {
        // given
        String stringDate = "2022-01-01";
        LocalDate date = LocalDate.parse(stringDate);

        ActivityDto.Article article = ActivityDto.Article.builder()
                .articleId(1L)
                .likeCount(10L)
                .title(CONTENT2)
                .commentCount(10L)
                .createdDate(date).build();

        ActivityDto.Article answer = ActivityDto.Article.builder()
                .articleId(2L)
                .title(CONTENT1)
                .likeCount(5L)
                .commentCount(10L)
                .createdDate(date).build();

        ActivityDto.Comment comment = ActivityDto.Comment.builder()
                .articleId(3L)
                .content(CONTENT1)
                .createdDate(date)
                .build();

        ActivityDto.Detail result = ActivityDto.Detail.builder()
                .answers(List.of(answer))
                .articles(List.of(article))
                .comments(List.of(comment))
                .total(3L)
                .build();

        given(userService.findActivityHistoryOn(any(LocalDate.class), any())).willReturn(result);

        // when
        ResultActions perform = mockMvc.perform(get("/users/{user-id}/dashboard/activities/{date}", 1, "2022-01-01"));

        // then
        perform.andExpect(status().isOk())
                .andDo(
                        document("대시보드_상세조회_성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("articles").type(ARRAY).description("게시글"),
                                fieldWithPath("articles[].articleId").type(NUMBER).description("게시글 id"),
                                fieldWithPath("articles[].title").type(STRING).description("게시글 제목"),
                                fieldWithPath("articles[].likeCount").type(NUMBER).description("게시글 좋아요 수"),
                                fieldWithPath("articles[].commentCount").type(NUMBER).description("게시글 댓글 수"),
                                fieldWithPath("articles[].createdDate").type(STRING).description("작성한 작성일자"),
                                fieldWithPath("answers").type(ARRAY).description("답변한 게시글"),
                                fieldWithPath("answers[].articleId").type(NUMBER).description("답변한 게시글 id"),
                                fieldWithPath("answers[].title").type(STRING).description("답변한 게시글 제목"),
                                fieldWithPath("answers[].likeCount").type(NUMBER).description("답변한 게시글 좋아요 수"),
                                fieldWithPath("answers[].commentCount").type(NUMBER).description("답변한 게시글 댓글 수"),
                                fieldWithPath("answers[].createdDate").type(STRING).description("답변한 게시글 작성일자"),
                                fieldWithPath("comments[]").type(ARRAY).description("작성 댓글"),
                                fieldWithPath("comments[].articleId").type(NUMBER).description("댓글 작성한 게시글의 id"),
                                fieldWithPath("comments[].content").type(STRING).description("댓글 내용"),
                                fieldWithPath("comments[].createdDate").type(STRING).description("작성 일자"),
                                fieldWithPath("total").type(NUMBER).description("총 활동량")
                                )
                )
                );
    }
}
