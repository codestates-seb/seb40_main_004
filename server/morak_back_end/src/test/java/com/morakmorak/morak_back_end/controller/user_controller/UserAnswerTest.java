package com.morakmorak.morak_back_end.controller.user_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.controller.UserController;
import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.ResponseMultiplePaging;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.service.auth_user_service.PointService;
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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.util.TestConstants.NOW_TIME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class UserAnswerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;
    @MockBean
    UserMapper userMapper;
    @MockBean
    UserService userService;
    @MockBean
    PointService pointService;
    UserDto.ResponseSimpleUserDto dtoUserInfo;
    AvatarDto.SimpleResponse dtoAvatar;
    @BeforeEach
    void setup() {
        dtoUserInfo =
                UserDto.ResponseSimpleUserDto.builder().userId(1L)
                        .nickname("nickname").grade(Grade.BRONZE).build();

        dtoAvatar =
                AvatarDto.SimpleResponse.builder().avatarId(1L)
                        .remotePath("remotePath").filename("fileName").build();
    }

    @Test
    @DisplayName("유저의 답변 목록에 대해 유효한 요청 시 200 반환")
    void getUserAnswers_success_1() throws Exception {
        List<AnswerDto.ResponseUserAnswerList> userAnswers = new ArrayList<>();
        AnswerDto.ResponseUserAnswerList answerPicked = AnswerDto.ResponseUserAnswerList.builder().articleId(1L).answerId(1L).content("픽미픽미픽미업 채택된 짱 멋진 답변입니다 후후후!").isPicked(true).answerLikeCount(3).commentCount(10).createdAt(NOW_TIME).userInfo(dtoUserInfo).build();
        AnswerDto.ResponseUserAnswerList answerNotPicked = AnswerDto.ResponseUserAnswerList.builder().articleId(2L).answerId(2L).content("채택되지 못한 답변입니다. 다음 기회에~").isPicked(false).answerLikeCount(3).commentCount(10).createdAt(NOW_TIME).userInfo(dtoUserInfo).build();
        userAnswers.add(answerPicked);
        userAnswers.add(answerNotPicked);

        List<Answer> answers = new ArrayList<>();
        answers.add(Answer.builder().id(1L).build());
        PageRequest pageable = PageRequest.of(0, 50);

        Page<Answer> userAnswersInPage = new PageImpl<>(answers, pageable, 2);
        ResponseMultiplePaging<AnswerDto.ResponseUserAnswerList> userAnswersResponsePaging = new ResponseMultiplePaging<>(userAnswers, userAnswersInPage);
        BDDMockito.given(userService.getUserAnswerList(any(),anyInt(),anyInt())).willReturn(userAnswersResponsePaging);

        ResultActions perform = mockMvc.perform(
                get("/users/{user-id}/answers", 1L)
                        .param("page", "1")
                        .param("size", "50")
        );
        perform.andExpect(status().isOk())
                .andDo(
                        document(
                                "유저 대시보드 답변목록 조회 성공_200",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        List.of(
                                                fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("답변을 리스트 형태로 보여줍니다."),
                                                fieldWithPath("data[].articleId").type(JsonFieldType.NUMBER).description("답변이 속한 게시글의 아이디입니다."),
                                                fieldWithPath("data[].answerId").type(JsonFieldType.NUMBER).description("답변의 아이디입니다."),
                                                fieldWithPath("data[].isPicked").type(JsonFieldType.BOOLEAN).description("답변이 채택 되었다면 true를 반환합니다."),
                                                fieldWithPath("data[].content").type(JsonFieldType.STRING).description("답변 내용입니다."),
                                                fieldWithPath("data[].answerLikeCount").type(JsonFieldType.NUMBER).description("답변의 좋아요수입니다."),
                                                fieldWithPath("data[].commentCount").type(JsonFieldType.NUMBER).description("답변의 댓글 갯수입니다."),
                                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("답변이 생성된 날짜입니다."),
                                                fieldWithPath("data[].userInfo.userId").type(JsonFieldType.NUMBER).description("유저의 아이디입니다."),
                                                fieldWithPath("data[].userInfo.nickname").type(JsonFieldType.STRING).description("유저의 닉네임입니다."),
                                                fieldWithPath("data[].userInfo.grade").type(JsonFieldType.STRING).description("유저의 등급입니다."),
                                                fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 보여질 페이지 입니다."),
                                                fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("한페이지에 들어갈 답변의 갯수 입니다. 기본 설정상 5개입니다."),
                                                fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 답변의 갯수 입니다."),
                                                fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 숫자입니다."),
                                                fieldWithPath("pageInfo.sort.empty").type(JsonFieldType.BOOLEAN).description("페이지가 비여있다면 true를 반환합니다."),
                                                fieldWithPath("pageInfo.sort.unsorted").type(JsonFieldType.BOOLEAN).description("페이지가 정렬되지 않았다면 true를 반환합니다."),
                                                fieldWithPath("pageInfo.sort.sorted").type(JsonFieldType.BOOLEAN).description("페이지가 정렬되었다면 true를 반환합니다.")
                                        )
                                )

                        )
                );
    }
    @Test
    @DisplayName("요청한 유저의 아이디가 존재하지 않거나 잘못된 경우 404 반환")
    void requestDashboard_failed() throws Exception {
        //given
        given(userService.getUserAnswerList(any(),anyInt(),anyInt())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        //when
        ResultActions perform = mockMvc.perform(
                get("/users/{user-id}/answers", 1L)
                        .param("page", "1")
                        .param("size", "50")
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document(
                        "유저 답변목록_조회_실패_404",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("user-id").description("존재하지 않는 유저의 아이디 혹은 잘못된 아이디 형식")
                        )
                ));
    }

}
