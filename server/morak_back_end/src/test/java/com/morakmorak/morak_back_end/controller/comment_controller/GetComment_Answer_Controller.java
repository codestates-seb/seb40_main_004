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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CommentController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class GetComment_Answer_Controller {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;
    @MockBean
    CommentService commentService;

    @Test
    @DisplayName("답변글에 댓글 조회 성공 시 200을 반환한다")
    void getComments_Answer_success_1() throws Exception {
        //given
        List<CommentDto.ResponseForAnswer> response = new ArrayList<>();

        CommentDto.ResponseForAnswer responseForAnswer = CommentDto.ResponseForAnswer.builder()
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
                .answerId(1L)
                .content("냥냐냥냐")
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();
        response.add(responseForAnswer);

        given(commentService.findAllCommentsOnAnswer(anyLong())).willReturn(response);
        ResultActions perform =
                mockMvc.perform(
                        get("/answers/{answer-id}/comments", 1L)
                                .param("page","0")
                                .param("size","10")
                );
        //then
        perform.andExpect(status().isOk())
                .andDo(document(
                        "답변 댓글 조회 성공_200",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                List.of(
                                        fieldWithPath("[].userInfo.userId").type(JsonFieldType.NUMBER).description("유저 식별자입니다"),
                                        fieldWithPath("[].userInfo.nickname").type(JsonFieldType.STRING).description("유저 닉네임입니다"),
                                        fieldWithPath("[].userInfo.grade").type(JsonFieldType.STRING).description("유저 등급입니다"),
                                        fieldWithPath("[].avatar.avatarId").type(JsonFieldType.NUMBER).description("프로필사진 식별자입니다"),
                                        fieldWithPath("[].avatar.filename").type(JsonFieldType.STRING).description("파일 이름입니다"),
                                        fieldWithPath("[].avatar.remotePath").type(JsonFieldType.STRING).description("유저 닉네임입니다"),
                                        fieldWithPath("[].answerId").type(JsonFieldType.NUMBER).description("답변글 식별자입니다"),
                                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("댓글 내용입니다"),
                                        fieldWithPath("[].commentId").type(JsonFieldType.NUMBER).description("댓글 식별자입니다"),
                                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("댓글 첫 작성일입니다."),
                                        fieldWithPath("[].lastModifiedAt").type(JsonFieldType.STRING).description("댓글 최신 수정일입니다")
                                )
                        )
                )
                );
    }
}
