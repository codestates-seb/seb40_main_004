package com.morakmorak.morak_back_end.controller.comment_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.CommentController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.morakmorak.morak_back_end.util.CommentTestConstants.VALID_COMMENT;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CommentController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class UpdateComment_Answer_Controller {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;
    @MockBean
    CommentService commentService;
    @Test
    @DisplayName("답변의 댓글 수정 시 유효한 데이터가 인입되었다면 200을 반환한다.")
    void updateComment_success_1() throws Exception {
        //given 유효한 댓글 수정 요청
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();
        CommentDto.ResponseForAnswer exampleComment = CommentDto.ResponseForAnswer.builder()
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

        List<CommentDto.ResponseForAnswer> commentList = new ArrayList<>();
        commentList.add(exampleComment);

        String json = objectMapper.writeValueAsString(request);
        BDDMockito.given(commentService.editCommentOnAnswer(any(),any(),any(),anyString())).willReturn(commentList);
        //when 유효한 input
        ResultActions perform = mockMvc.perform(patch("/answers/{answer-id}/comments/{comment-id}", 1L,1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN)
        );
        perform.andExpect(status().isOk())
                .andDo(
                        document(
                                "답변의 댓글 수정 성공_200",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("access token")
                                ),
                                requestFields(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용")
                                ),
                                responseFields(
                                        List.of(
                                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("응답 데이터"),
                                                fieldWithPath("[].userInfo.userId").type(JsonFieldType.NUMBER).description("유저 식별자입니다"),
                                                fieldWithPath("[].userInfo.nickname").type(JsonFieldType.STRING).description("유저 닉네임입니다"),
                                                fieldWithPath("[].userInfo.grade").type(JsonFieldType.STRING).description("유저 등급입니다"),
                                                fieldWithPath("[].avatar.avatarId").type(JsonFieldType.NUMBER).description("프로필사진 식별자입니다"),
                                                fieldWithPath("[].avatar.filename").type(JsonFieldType.STRING).description("파일 이름입니다"),
                                                fieldWithPath("[].avatar.remotePath").type(JsonFieldType.STRING).description("유저 닉네임입니다"),
                                                fieldWithPath("[].answerId").type(JsonFieldType.NUMBER).description("글 식별자입니다"),
                                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("댓글 내용입니다"),
                                                fieldWithPath("[].commentId").type(JsonFieldType.NUMBER).description("댓글 식별자입니다"),
                                                fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("댓글 첫 작성일입니다."),
                                                fieldWithPath("[].lastModifiedAt").type(JsonFieldType.STRING).description("댓글 최신 수정일입니다")
                                        )
                                )
                        )
                );
    }
    @Test
    @DisplayName("답변 댓글 수정 시 수정 권한이 없다면 409를 반환한다.")
    void updateComment_failed_1() throws Exception {
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();
        String json = objectMapper.writeValueAsString(request);
        BDDMockito.given(commentService.editCommentOnAnswer(any(), any(), any(), anyString())).willThrow(new BusinessLogicException(ErrorCode.CANNOT_ACCESS_COMMENT));
        //when 권한 없는 유저가 수정 요청을 보냈을 때
        ResultActions perform = mockMvc.perform(patch("/answers/{answer-id}/comments/{comment-id}", 1L,1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN)
        );
        //then 409 conflict 발생
        perform
                .andExpect(status().isConflict())
                .andDo(
                        document(
                                "update comment failed by unauthorized user",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("권한 없는 사용자의 access token")
                                ),
                                requestFields(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용")
                                )
                        )
                );
    }
}
