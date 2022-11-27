package com.morakmorak.morak_back_end.controller.answer_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.AnswerController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.mapper.AnswerMapper;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.service.AnswerService;
import com.morakmorak.morak_back_end.service.FileService;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.TestConstants.NOW_TIME;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AnswerController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
@WithMockUser
public class GetAnswerControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    JwtArgumentResolver jwtArgumentResolver;
    @MockBean
    AnswerService answerService;
    @MockBean
    FileService fileService;
    @MockBean
    AnswerMapper answerMapper;

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
    @DisplayName("비로그인 유저의 유효한 답변 조회 요청인 경우 200 ok 반환")
    void getAnswer_success_1() throws Exception {
        //given
        List<AnswerDto.ResponseListTypeAnswer> dtoResponseListTypeAnswer = new ArrayList<>();
        AnswerDto.ResponseListTypeAnswer responseListTypeAnswer = AnswerDto.ResponseListTypeAnswer.builder()
                .answerId(1L)
                .answerLikeCount(3)
                .userInfo(dtoUserInfo)
                .avatar(dtoAvatar)
                .isPicked(false)
                .isLiked(true)
                .content("contentcontentcontentcontentcontent")
                .commentCount(10)
                .commentPreview(CommentDto.Response.builder().commentId(1L).answerId(1L).content("멋진 코딩실력을 가졌군요! 부럽다!").avatar(dtoAvatar).userInfo(dtoUserInfo).createdAt(NOW_TIME).lastModifiedAt(NOW_TIME).build())
                .createdAt(NOW_TIME)
                .build();

        dtoResponseListTypeAnswer.add(responseListTypeAnswer);

        List<Answer> answers = new ArrayList<>();
        answers.add(Answer.builder().id(1L).build());
        PageRequest pageable = PageRequest.of(0, 5,Sort.by("createdAt").descending());
        Page<Answer> answerInPage = new PageImpl<>(answers, pageable, 1);

        ResponseMultiplePaging<AnswerDto.ResponseListTypeAnswer> answerResponseMultiplePaging =
                new ResponseMultiplePaging<>(dtoResponseListTypeAnswer, answerInPage);
        BDDMockito.given(answerService.readAllAnswers(anyLong(), anyInt(), anyInt())).willReturn(answerResponseMultiplePaging);
        BDDMockito.given(answerService.readAllAnswersForUser(anyLong(), any(), anyInt(), anyInt())).willReturn(answerResponseMultiplePaging);

        //when
        ResultActions perform = mockMvc.perform(
                get("/articles/1/answers")
                        .param("page", "1")
                        .param("size", "5")
        );

        //then
        perform.andExpect(status().isOk())
                .andDo(
                        document(
                                "비회원_답변 조회 성공_200",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("article-id").description("게시글의 아이디 입니다.")
                                ),
                                responseFields(
                                        List.of(
                                                fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("답변을 리스트 형태로 보여줍니다."),
                                                fieldWithPath("data[].answerId").type(JsonFieldType.NUMBER).description("답변의 아이디입니다."),
                                                fieldWithPath("data[].content").type(JsonFieldType.STRING).description("답변 내용입니다."),
                                                fieldWithPath("data[].answerLikeCount").type(JsonFieldType.NUMBER).description("답변의 좋아요수입니다."),
                                                fieldWithPath("data[].isPicked").type(JsonFieldType.BOOLEAN).description("답변이 채택 되었다면 true를 반환합니다."),
                                                fieldWithPath("data[].isLiked").type(JsonFieldType.BOOLEAN).description("유저가 좋아요한 답변이라면 true를 반환합니다."),
                                                fieldWithPath("data[].isPicked").type(JsonFieldType.NULL).description("비로그인 유저에게는 좋아요 여부가 조회되지 않습니다."),
                                                fieldWithPath("data[].commentCount").type(JsonFieldType.NUMBER).description("답변의 댓글 갯수입니다."),
                                                fieldWithPath("data[].commentPreview.commentId").type(JsonFieldType.NUMBER).description("답변의 댓글 식별자입니다."),
                                                fieldWithPath("data[].commentPreview.answerId").type(JsonFieldType.NUMBER).description("답변의 댓글이 소속된 답변입니다."),
                                                fieldWithPath("data[].commentPreview.articleId").type(JsonFieldType.NULL).description("답변 댓글이므로 게시글 식별자는 비어있습니다."),
                                                fieldWithPath("data[].commentPreview.content").type(JsonFieldType.STRING).description("답변의 댓글 내용입니다."),
                                                fieldWithPath("data[].commentPreview.userInfo.userId").type(JsonFieldType.NUMBER).description("답변의 댓글 작성자 아이디입니다."),
                                                fieldWithPath("data[].commentPreview.userInfo.nickname").type(JsonFieldType.STRING).description("답변의 댓글 작성자 닉네임입니다."),
                                                fieldWithPath("data[].commentPreview.userInfo.grade").type(JsonFieldType.STRING).description("답변의 댓글 작성자 등급입니다."),
                                                fieldWithPath("data[].commentPreview.avatar.avatarId").type(JsonFieldType.NUMBER).description("답변의 댓글 작성자 프로필파일의 아이디 입니다."),
                                                fieldWithPath("data[].commentPreview.avatar.filename").type(JsonFieldType.STRING).description("답변의 댓글 작성자 프로필 파일의 이름입니다."),
                                                fieldWithPath("data[].commentPreview.avatar.remotePath").type(JsonFieldType.STRING).description("답변의 댓글 작성자 프로필 파일의 경로입니다."),
                                                fieldWithPath("data[].commentPreview.createdAt").type(JsonFieldType.STRING).description("답변의 댓글 생성일입니다."),
                                                fieldWithPath("data[].commentPreview.lastModifiedAt").type(JsonFieldType.STRING).description("답변의 댓글 마지막 수정일입니다."),
                                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("답변이 생성된 날짜입니다."),
                                                fieldWithPath("data[].userInfo.userId").type(JsonFieldType.NUMBER).description("유저의 아이디입니다."),
                                                fieldWithPath("data[].userInfo.nickname").type(JsonFieldType.STRING).description("유저의 닉네임입니다."),
                                                fieldWithPath("data[].userInfo.grade").type(JsonFieldType.STRING).description("유저의 등급입니다."),
                                                fieldWithPath("data[].avatar.avatarId").type(JsonFieldType.NUMBER).description("아바타 파일의 아이디 입니다."),
                                                fieldWithPath("data[].avatar.filename").type(JsonFieldType.STRING).description("아바타 파일의 이름입니다."),
                                                fieldWithPath("data[].avatar.remotePath").type(JsonFieldType.STRING).description("아바타 파일의 경로입니다."),
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
    @DisplayName("회원의 유효한 답변 조회 요청 시 200 반환")
    void getAnswers_ForUser_success_2() throws Exception {
        //회원이 보내는 요청
        //받는 답변 하나는 좋아요 하나는 안좋아요
        List<AnswerDto.ResponseListTypeAnswer> dtoAnswerList = new ArrayList<>();

        AnswerDto.ResponseListTypeAnswer responseLiked = AnswerDto.ResponseListTypeAnswer.builder()
                .answerId(1L)
                .answerLikeCount(3)
                .userInfo(dtoUserInfo)
                .avatar(dtoAvatar)
                .isPicked(false)
                .isLiked(false)
                .content("좋아요 표시한 답변")
                .commentCount(10)
                .commentPreview(CommentDto.Response.builder().commentId(1L).answerId(1L).content("멋진 코딩실력을 가졌군요! 부럽다!").avatar(dtoAvatar).userInfo(dtoUserInfo).createdAt(NOW_TIME).lastModifiedAt(NOW_TIME).build())
                .createdAt(NOW_TIME)
                .build();

        AnswerDto.ResponseListTypeAnswer responseNotLiked = AnswerDto.ResponseListTypeAnswer.builder()
                .answerId(2L)
                .answerLikeCount(3)
                .userInfo(dtoUserInfo)
                .avatar(dtoAvatar)
                .isPicked(false)
                .isLiked(true)
                .content("유저가 좋아요를 누르지 않은 답변입니다.")
                .commentCount(10)
                .commentPreview(CommentDto.Response.builder().commentId(2L).answerId(1L).content("김코딩코딩씨 모락모락 구운감자가 되어주세요.").avatar(dtoAvatar).userInfo(dtoUserInfo).createdAt(NOW_TIME).lastModifiedAt(NOW_TIME).build())
                .createdAt(NOW_TIME)
                .build();

        dtoAnswerList.add(responseLiked);
        dtoAnswerList.add(responseNotLiked);

        List<Answer> answers = new ArrayList<>();
        answers.add(Answer.builder().id(1L).build());

        PageRequest pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Page<Answer> answerInPage = new PageImpl<>(answers, pageable, 2);
        ResponseMultiplePaging<AnswerDto.ResponseListTypeAnswer> answerResponseMultiplePaging =
                new ResponseMultiplePaging<>(dtoAnswerList, answerInPage);

        BDDMockito.given(answerMapper.answerToResponseListTypeAnswer(any(), anyBoolean(), anyBoolean(), anyInt(),any(),anyInt())).willReturn(responseLiked);
        BDDMockito.given(answerService.readAllAnswersForUser(any(), any(), anyInt(), anyInt())).willReturn(answerResponseMultiplePaging);
        ResultActions perform = mockMvc.perform(
                get("/articles/1/answers")
                        .param("page", "1")
                        .param("size", "5")
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );

        //then
        perform.andExpect(status().isOk())
                .andDo(
                        document(
                                "회원_답변 조회 성공_200",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("access token")
                                ),
                                pathParameters(
                                        parameterWithName("article-id").description("게시글의 아이디입니다.")
                                ),
                                responseFields(
                                        List.of(
                                                fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("답변을 리스트 형태로 보여줍니다."),
                                                fieldWithPath("data[].answerId").type(JsonFieldType.NUMBER).description("답변의 아이디입니다."),
                                                fieldWithPath("data[].content").type(JsonFieldType.STRING).description("답변 내용입니다."),
                                                fieldWithPath("data[].answerLikeCount").type(JsonFieldType.NUMBER).description("답변의 좋아요수입니다."),
                                                fieldWithPath("data[].isPicked").type(JsonFieldType.BOOLEAN).description("답변이 채택 되었다면 true를 반환합니다."),
                                                fieldWithPath("data[].isLiked").type(JsonFieldType.BOOLEAN).description("유저가 좋아요를 눌렀다면 true를 반환합니다."),
                                                fieldWithPath("data[].commentCount").type(JsonFieldType.NUMBER).description("답변의 댓글 갯수입니다."),
                                                fieldWithPath("data[].commentPreview.commentId").type(JsonFieldType.NUMBER).description("답변의 댓글 식별자입니다."),
                                                fieldWithPath("data[].commentPreview.answerId").type(JsonFieldType.NUMBER).description("답변의 댓글이 소속된 답변입니다."),
                                                fieldWithPath("data[].commentPreview.articleId").type(JsonFieldType.NULL).description("답변 댓글이므로 게시글 식별자는 비어있습니다."),
                                                fieldWithPath("data[].commentPreview.content").type(JsonFieldType.STRING).description("답변의 댓글 내용입니다."),
                                                fieldWithPath("data[].commentPreview.userInfo.userId").type(JsonFieldType.NUMBER).description("답변의 댓글 작성자 아이디입니다."),
                                                fieldWithPath("data[].commentPreview.userInfo.nickname").type(JsonFieldType.STRING).description("답변의 댓글 작성자 닉네임입니다."),
                                                fieldWithPath("data[].commentPreview.userInfo.grade").type(JsonFieldType.STRING).description("답변의 댓글 작성자 등급입니다."),
                                                fieldWithPath("data[].commentPreview.avatar.avatarId").type(JsonFieldType.NUMBER).description("답변의 댓글 작성자 프로필파일의 아이디 입니다."),
                                                fieldWithPath("data[].commentPreview.avatar.filename").type(JsonFieldType.STRING).description("답변의 댓글 작성자 프로필 파일의 이름입니다."),
                                                fieldWithPath("data[].commentPreview.avatar.remotePath").type(JsonFieldType.STRING).description("답변의 댓글 작성자 프로필 파일의 경로입니다."),
                                                fieldWithPath("data[].commentPreview.createdAt").type(JsonFieldType.STRING).description("답변의 댓글 생성일입니다."),
                                                fieldWithPath("data[].commentPreview.lastModifiedAt").type(JsonFieldType.STRING).description("답변의 댓글 마지막 수정일입니다."),
                                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("답변이 생성된 날짜입니다."),
                                                fieldWithPath("data[].userInfo.userId").type(JsonFieldType.NUMBER).description("유저의 아이디입니다."),
                                                fieldWithPath("data[].userInfo.nickname").type(JsonFieldType.STRING).description("유저의 닉네임입니다."),
                                                fieldWithPath("data[].userInfo.grade").type(JsonFieldType.STRING).description("유저의 등급입니다."),
                                                fieldWithPath("data[].avatar.avatarId").type(JsonFieldType.NUMBER).description("아바타 파일의 아이디 입니다."),
                                                fieldWithPath("data[].avatar.filename").type(JsonFieldType.STRING).description("아바타 파일의 이름입니다."),
                                                fieldWithPath("data[].avatar.remotePath").type(JsonFieldType.STRING).description("아바타 파일의 경로입니다."),
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
}
