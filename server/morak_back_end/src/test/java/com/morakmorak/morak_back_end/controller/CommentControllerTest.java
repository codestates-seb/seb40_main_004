package com.morakmorak.morak_back_end.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.service.CommentService;
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

import java.util.List;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CommentController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
class CommentControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;
    @MockBean
    CommentService commentService;

    String VALID_CONTENT = "유효한 댓글내용입니다.";
    @Test
    @DisplayName("댓글을 등록 시 유효하지 않은 Dto에 의해 실패하면 400을 반환한다.")
    void postCommentOnArticle() throws Exception {
        //given
        CommentDto.RequestPost request = CommentDto.RequestPost.builder()
                .content(null).build();
        //when 잘못된 dto json 인입
        String json = objectMapper.writeValueAsString(request);
        //when
        ResultActions perform =
                mockMvc.perform(
                        post("/articles/{article-id}/comments", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header(JWT_HEADER, ACCESS_TOKEN)
                );

        //then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").doesNotExist())
                .andDo(
                        document(
                                "task post comment failed caused by invalid dto",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("access token")
                                ),
                                requestFields(
                                        fieldWithPath("content").description("댓글이 비어 있으므로 등록될 수 없습니다.")
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시글 등록 성공 시 200을 반환한다")
    void postComment_success_1() throws Exception {
        //given
        CommentDto.RequestPost request = CommentDto.RequestPost.builder()
            .content(VALID_CONTENT).build();
        CommentDto.ReponsePost response = CommentDto.ReponsePost.builder()
                .userInfo(UserDto.ResponseForCommentUserInfo.builder()
                        .userId(1L)
                        .nickname("NICKNAME")
                        .build())
                .avatar(AvatarDto.SimpleResponse.builder()
                        .avatarId(1L)
                        .fileName("FILENAME")
                        .remotePath("FILEPATH")
                        .build())
                .commentId(1L)
                .articleId(1L)
                .content("냥냐냥냐").build();
        //when 적절한 json 인입
        String json = objectMapper.writeValueAsString(request);
        BDDMockito.given(commentService.makeComment(any(),any(),any())).willReturn(response);
        ResultActions perform =
                mockMvc.perform(
                        post("/articles/{article-id}/comments", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header(JWT_HEADER, ACCESS_TOKEN)
                );
        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andDo(document(
                        "task post comment success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용")                     ),
                        responseFields(
                                List.of(
                                        fieldWithPath("userInfo.userId").type(JsonFieldType.NUMBER).description("유저식별자"),
                                        fieldWithPath("userInfo.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                        fieldWithPath("avatar.avatarId").type(JsonFieldType.NUMBER).description("유저 닉네임"),
                                        fieldWithPath("avatar.fileName").type(JsonFieldType.STRING).description("유저 닉네임"),
                                        fieldWithPath("avatar.remotePath").type(JsonFieldType.STRING).description("유저 닉네임"),
                                        fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("글 식별자"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                                        fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 식별자")
                                )
                        )
                ));
    }
}

