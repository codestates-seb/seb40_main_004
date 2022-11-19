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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static com.morakmorak.morak_back_end.util.ArticleTestConstants.REQUEST_FILE_WITH_IDS;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AnswerController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class PostAnswerControllerTest {
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
    void postAnswer_success_1() throws Exception {
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
                                                fieldWithPath("avatar.fileName").type(JsonFieldType.STRING).description("파일 이름입니다"),
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
}
