package com.morakmorak.morak_back_end.controller.user_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.controller.UserController;
import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.auth_user_service.PointService;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.exception.ErrorCode.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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

    @MockBean
    PointService pointService;

    JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    public void init() {
        jwtTokenUtil = new JwtTokenUtil(SECRET_KEY, REFRESH_KEY);
    }

    @Test
    @DisplayName("??????????????? ????????? ????????? ?????? ??? ?????? ?????? 404 NotFound??? ????????????.")
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
                        document("????????????_????????????_??????_404",
                                getDocumentRequest(),
                                getDocumentResponse()
                        )
                );
    }

    @Test
    @DisplayName("??????????????? ?????? ????????? ???????????? ?????? ?????? 400 ????????? ????????????.")
    public void getUserActivity_failed2() throws Exception {
        // given
        String stringDate = "2022-01-01";
        LocalDate date = LocalDate.parse(stringDate);

        // when
        ResultActions perform = mockMvc.perform(get("/users/{user-id}/profiles/dashboard/activities/{date}", 1, "2022-01-101"));

        // then
        perform.andExpect(status().isNotFound())
                .andDo(
                        document("????????????_????????????_??????_400",
                                getDocumentRequest(),
                                getDocumentResponse()
                        )
                );
    }

    @Test
    @DisplayName("??????????????? ?????? ????????? ????????? ?????? ?????? 416 ????????? ????????????.")
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
                        document("????????????_????????????_??????_416",
                                getDocumentRequest(),
                                getDocumentResponse()
                        )
                );
    }

    @Test
    @DisplayName("??????????????? ????????? ????????? ????????? ?????? ????????? ???????????? ????????????.")
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
                        document("????????????_????????????_??????_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("articles").type(ARRAY).description("?????????"),
                                fieldWithPath("articles[].articleId").type(NUMBER).description("????????? id"),
                                fieldWithPath("articles[].title").type(STRING).description("????????? ??????"),
                                fieldWithPath("articles[].likeCount").type(NUMBER).description("????????? ????????? ???"),
                                fieldWithPath("articles[].commentCount").type(NUMBER).description("????????? ?????? ???"),
                                fieldWithPath("articles[].createdDate").type(STRING).description("????????? ????????????"),
                                fieldWithPath("answers").type(ARRAY).description("????????? ?????????"),
                                fieldWithPath("answers[].articleId").type(NUMBER).description("????????? ????????? id"),
                                fieldWithPath("answers[].title").type(STRING).description("????????? ????????? ??????"),
                                fieldWithPath("answers[].likeCount").type(NUMBER).description("????????? ????????? ????????? ???"),
                                fieldWithPath("answers[].commentCount").type(NUMBER).description("????????? ????????? ?????? ???"),
                                fieldWithPath("answers[].createdDate").type(STRING).description("????????? ????????? ????????????"),
                                fieldWithPath("comments[]").type(ARRAY).description("?????? ??????"),
                                fieldWithPath("comments[].articleId").type(NUMBER).description("?????? ????????? ???????????? id"),
                                fieldWithPath("comments[].content").type(STRING).description("?????? ??????"),
                                fieldWithPath("comments[].createdDate").type(STRING).description("?????? ??????"),
                                fieldWithPath("total").type(NUMBER).description("??? ?????????")
                                )
                )
                );
    }
}
