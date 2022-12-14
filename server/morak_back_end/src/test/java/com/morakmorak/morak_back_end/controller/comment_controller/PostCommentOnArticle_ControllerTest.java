package com.morakmorak.morak_back_end.controller.comment_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.CommentController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.enums.Grade;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CommentController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
class PostCommentOnArticle_ControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;
    @MockBean
    CommentService commentService;

    String VALID_CONTENT = "????????? ?????????????????????.";

    @Test
    @DisplayName("????????? ?????? ??? ???????????? ?????? Dto??? ?????? ???????????? 400??? ????????????.")
    void postCommentOnArticle() throws Exception {
        //given
        CommentDto.Request request = CommentDto.Request.builder()
                .content(null).build();
        //when ????????? dto json ??????
        String json = objectMapper.writeValueAsString(request);
//        BDDMockito.given(commentService.makeComment(any(),any(),any(),anyBoolean())).willThrow()
        //when
        ResultActions perform =
                mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/articles/{article-id}/comments", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header(JWT_HEADER, ACCESS_TOKEN)
                );

        //then
        perform
                .andExpect(status().isBadRequest())
                .andDo(
                        document(
                                "?????? ?????????????????? ??????_400",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("access token")
                                ),
                                requestFields(
                                        fieldWithPath("content").description("????????? ?????? ???????????? ????????? ??? ????????????.")
                                )
                        )
                );
    }

    @Test
    @DisplayName("?????? ?????? ?????? ??? 200??? ????????????")
    void postComment_success_1() throws Exception {
        //given
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_CONTENT).build();

        CommentDto.Response exampleComment = CommentDto.Response.builder()
                .userInfo(UserDto.ResponseSimpleUserDto.builder()
                        .userId(1L)
                        .nickname("NICKNAME")
                        .grade(Grade.GOLD)
                        .build())
                .avatar(AvatarDto.SimpleResponse.builder()
                        .avatarId(1L)
                        .filename("FILENAME")
                        .remotePath("FILEPATH")
                        .build())
                .commentId(1L)
                .articleId(1L)
                .content("????????????")
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        List<CommentDto.Response> response = new ArrayList<>();
        response.add(exampleComment);

        //when ????????? json ??????
        String json = objectMapper.writeValueAsString(request);
        BDDMockito.given(commentService.makeComment(any(), any(), any(), anyBoolean())).willReturn(response);
        ResultActions perform =
                mockMvc.perform(
                        post("/articles/{article-id}/comments", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header(JWT_HEADER, ACCESS_TOKEN)
                );
        //then
        perform.andExpect(status().isCreated())
                .andDo(document(
                        "?????? ???????????? ?????? ??????_201",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????")),
                        responseFields(
                                List.of(
                                        fieldWithPath("[].userInfo.userId").type(JsonFieldType.NUMBER).description("?????? ??????????????????"),
                                        fieldWithPath("[].userInfo.nickname").type(JsonFieldType.STRING).description("?????? ??????????????????"),
                                        fieldWithPath("[].userInfo.grade").type(JsonFieldType.STRING).description("?????? ???????????????"),
                                        fieldWithPath("[].avatar.avatarId").type(JsonFieldType.NUMBER).description("??????????????? ??????????????????"),
                                        fieldWithPath("[].avatar.filename").type(JsonFieldType.STRING).description("?????? ???????????????"),
                                        fieldWithPath("[].avatar.remotePath").type(JsonFieldType.STRING).description("?????? ??????????????????"),
                                        fieldWithPath("[].articleId").type(JsonFieldType.NUMBER).description("??? ??????????????????"),
                                        fieldWithPath("[].answerId").type(JsonFieldType.NULL).description("???????????? ?????? ??????????????????"),
                                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("?????? ???????????????"),
                                        fieldWithPath("[].commentId").type(JsonFieldType.NUMBER).description("?????? ??????????????????"),
                                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("?????? ??? ??????????????????."),
                                        fieldWithPath("[].lastModifiedAt").type(JsonFieldType.STRING).description("?????? ?????? ??????????????????")
                                )
                        )
                ));
    }
}
