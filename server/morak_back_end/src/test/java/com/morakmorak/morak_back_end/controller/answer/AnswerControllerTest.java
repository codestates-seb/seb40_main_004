package com.morakmorak.morak_back_end.controller.answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.AnswerController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Category;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.service.AnswerService;
import com.morakmorak.morak_back_end.service.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static com.morakmorak.morak_back_end.util.ArticleTestConstants.REQUEST_FILE_WITH_IDS;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AnswerController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class AnswerControllerTest {
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

    @Test
    @DisplayName("카테고리가 question이 아닌 경우 409 예외 반환")
    void postFile_failed_1() throws Exception {
        //given
        AnswerDto.RequestPostAnswer request = AnswerDto.RequestPostAnswer.builder().content("유효한 길이의 게시물입니다.")
                .fileIdList(REQUEST_FILE_WITH_IDS)
                .build();

        String json = objectMapper.writeValueAsString(request);

        Article invalidArticle_info = Article.builder().category(Category.builder().name(CategoryName.INFO).build()).build();

        BDDMockito.given(answerService.postAnswer(any(),any(),any(),anyList())).willThrow(new BusinessLogicException(ErrorCode.UNABLE_TO_ANSWER));
        //when
        ResultActions perform = mockMvc.perform(post("/articles/1/answers/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN)
        );

        //then
        perform.andExpect(status().isConflict())
                .andDo(
                        document(
                                "task post answer failed caused by invalid article category",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("access token")
                                ),
                                requestFields(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("답변 본문입니다."),
                                        fieldWithPath("fileIdList[].fileId").type(JsonFieldType.NUMBER).description("첨부파일 식별자 목록입니다.")
                                )
                        )
                );
    }
    @Test
    @DisplayName("유효한 등록 요청인 경우 201 created 반환")
    void postFile_success_1() throws Exception {
        //given
        AnswerDto.RequestPostAnswer request = AnswerDto.RequestPostAnswer.builder().content("유효한 길이의 게시물입니다.")
                .fileIdList(REQUEST_FILE_WITH_IDS)
                .build();

        String json = objectMapper.writeValueAsString(request);

        AnswerDto.SimpleResponsePostAnswer response= AnswerDto.SimpleResponsePostAnswer.builder()
                .answerId(1L)
                .userInfo(UserDto.ResponseSimpleUserDto.of(USER1))
                .avatar(AvatarDto.SimpleResponse.of(AVATAR))
                .content(CONTENT1)
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        BDDMockito.given(answerService.postAnswer(any(),any(),any(),anyList())).willReturn(response);
        //when
        ResultActions perform = mockMvc.perform(post("/articles/1/answers/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN)
        );

        //then
        perform.andExpect(status().isCreated())
                .andDo(
                        document(
                                "task post answer succeeded",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("access token")
                                ),
                                requestFields(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("답변 본문입니다."),
                                        fieldWithPath("fileIdList[].fileId").type(JsonFieldType.NUMBER).description("첨부파일 식별자 목록입니다.")
                                ),
                                responseFields(
                                        List.of(
                                                fieldWithPath("userInfo.userId").type(JsonFieldType.NUMBER).description("유저 식별자입니다"),
                                                fieldWithPath("userInfo.nickname").type(JsonFieldType.STRING).description("유저 닉네임입니다"),
                                                fieldWithPath("userInfo.grade").type(JsonFieldType.STRING).description("유저 등급입니다"),
                                                fieldWithPath("avatar.avatarId").type(JsonFieldType.NUMBER).description("프로필사진 식별자입니다"),
                                                fieldWithPath("avatar.filename").type(JsonFieldType.STRING).description("파일 이름입니다"),
                                                fieldWithPath("avatar.remotePath").type(JsonFieldType.STRING).description("유저 닉네임입니다"),
                                                fieldWithPath("content").type(JsonFieldType.STRING).description("답글 내용입니다"),
                                                fieldWithPath("answerId").type(JsonFieldType.NUMBER).description("답글 식별자입니다"),
                                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("첫 작성일입니다."),
                                                fieldWithPath("lastModifiedAt").type(JsonFieldType.STRING).description("최신 수정일입니다")
                                        )
                                )
                        )
                );
    }

    @Test
    @DisplayName("로그인한 회원이 답변글의 좋아요를 눌렀을때 처음 좋아요를 눌렀을 경우 좋아요 갯수가 1개 증가하고 201 코드를 던진다.")
    public void pressLikeButton_suc() throws Exception{
        //given
        Long answerId = 1L;
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();

        AnswerDto.ResponseAnswerLike responseArticleLike =
                AnswerDto.ResponseAnswerLike.builder().answerId(1L).userId(1L).isLiked(true).likeCount(1).build();

        given(answerService.pressLikeButton(anyLong(), any())).willReturn(responseArticleLike);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/1/answers/"+answerId+"/likes")
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.answerId").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.isLiked").value(true))
                .andExpect(jsonPath("$.likeCount").value(1))
                .andDo(document(
                        "다변글_좋아요_처음누를때_성공_201",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("answerId").type(JsonFieldType.NUMBER).description("답변글의 아이디 입니다."),
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저의 아이디 입니다."),
                                        fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("해당 유저가 좋아요를 눌렀는지 안눌렀지를 보여줍니다,"),
                                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("해당 게시글의 좋아요 숫자입니다.")
                                )
                        )
                ));
    }
    @Test
    @DisplayName("로그인한 회원이 답변글의 누른 좋아요를 취소할 경우 좋아요 갯수가 1개 감소하고 201 코드를 던진다.")
    public void pressLikeButton_suc2() throws Exception{
        //given
        Long answerId = 1L;
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();

        AnswerDto.ResponseAnswerLike responseArticleLike =
                AnswerDto.ResponseAnswerLike.builder().answerId(1L).userId(1L).isLiked(true).likeCount(0).build();

        given(answerService.pressLikeButton(anyLong(), any())).willReturn(responseArticleLike);

        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/articles/1/answers/"+answerId+"/likes")
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.answerId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.isLiked").value(true))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andDo(document(
                        "답변글_좋아요_두번_누를때_(취소)_성공_201",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("answerId").type(JsonFieldType.NUMBER).description("답변글의 아이디 입니다."),
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저의 아이디 입니다."),
                                        fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("해당 유저가 좋아요를 눌렀는지 안눌렀지를 보여줍니다,"),
                                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("해당 게시글의 좋아요 숫자입니다.")
                                )
                        )
                ));
    }
    @Test
    @DisplayName("로그인하지 않은 유저가 좋아요를 누른다면, 404 User Not Found 에러를 던진다.")
    public void pressLikeButton_fail1() throws Exception{
        //given
        Long answerId = 1L;

        given(answerService.pressLikeButton(anyLong(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/articles/1/answers/"+answerId+"/likes")
        );

        //then
        perform.andExpect(status().isNotFound())
                .andDo(document(
                        "로그인하지않은_회원이_좋아요를_누를시_실패_404",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("존재하지 않는 답변글에 좋아요를 누른다면, 404 Article Not Found 에러를 던진다.")
    public void pressLikeButton_fail2() throws Exception{
        //given
        Long answerId = 1L;

        given(answerService.pressLikeButton(anyLong(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ARTICLE_NOT_FOUND));

        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/articles/1/answers/"+answerId+"/likes")
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );

        //then
        perform.andExpect(status().isNotFound())
                .andDo(document(
                        "로그인한_회원이_존재하지_않는_답변글에_좋아요를_누를시_실패_404",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        )));
    }
}
