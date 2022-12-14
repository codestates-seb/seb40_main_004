package com.morakmorak.morak_back_end.controller.comment_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.CommentController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.Avatar;
import com.morakmorak.morak_back_end.entity.Comment;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
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

import static com.morakmorak.morak_back_end.entity.enums.Grade.GOLD;
import static com.morakmorak.morak_back_end.util.CommentTestConstants.VALID_COMMENT;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest({CommentController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class GetCommentsOnAnswer_ControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;
    @MockBean
    CommentService commentService;
    Avatar dbAvatar;
    User dbUser;
    Answer dbAnswer;
    Comment dbComment;

    @BeforeEach
    public void setUp() throws Exception {
        String VALID_CONTENT = "????????? ?????????????????????.";

        this.dbAvatar = Avatar.builder()
                .id(ID1)
                .originalFilename("randomfilename")
                .remotePath("randomremotepath")
                .build();

        this.dbUser = User.builder()
                .id(ID1)
                .email(EMAIL1)
                .nickname(NICKNAME1)
                .password(PASSWORD1)
                .avatar(dbAvatar)
                .grade(GOLD)
                .build();

        this.dbAnswer = Answer.builder()
                .id(ID1)
                .user(dbUser)
                .build();

        this.dbComment = Comment.builder()
                .id(ID1)
                .user(dbUser)
                .answer(dbAnswer)
                .content(VALID_CONTENT)
                .build();
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ??? ????????? ??????????????????, ???????????? ??? 200 ??? ?????????????????? ????????????.")
    void updateComment_Answer_success_1() throws Exception {
        //given ????????? ?????? ?????? ??????
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();


        CommentDto.Response exampleComment = CommentDto.Response.builder()
                .userInfo(UserDto.ResponseSimpleUserDto.of(dbComment.getUser()))
                .avatar(AvatarDto.SimpleResponse.of(dbComment.getUser().getAvatar()))
                .commentId(2L)
                .answerId(dbComment.getAnswer().getId())
                .content("???????????? ??????????????????.")
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        List<CommentDto.Response> commentList = new ArrayList<>();
        commentList.add(exampleComment);

        String json = objectMapper.writeValueAsString(request);
        BDDMockito.given(commentService.findAllComments(any(),anyBoolean())).willReturn(commentList);

        //when ????????? input
        ResultActions perform = mockMvc.perform(get("/answers/{answer-id}/comments", dbAnswer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN)
        );
        perform.andExpect(status().isOk())
                .andDo(
                        document(
                                "?????? ???????????? ?????? ??????_200",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("access token")
                                ),
                                requestFields(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????")
                                ),
                                responseFields(
                                        List.of(
                                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("?????? ?????????"),
                                                fieldWithPath("[].userInfo.userId").type(JsonFieldType.NUMBER).description("?????? ??????????????????"),
                                                fieldWithPath("[].userInfo.nickname").type(JsonFieldType.STRING).description("?????? ??????????????????"),
                                                fieldWithPath("[].userInfo.grade").type(JsonFieldType.STRING).description("?????? ???????????????"),
                                                fieldWithPath("[].avatar.avatarId").type(JsonFieldType.NUMBER).description("??????????????? ??????????????????"),
                                                fieldWithPath("[].avatar.filename").type(JsonFieldType.STRING).description("?????? ???????????????"),
                                                fieldWithPath("[].avatar.remotePath").type(JsonFieldType.STRING).description("?????? ??????????????????"),
                                                fieldWithPath("[].articleId").type(JsonFieldType.NULL).description("???????????? ??? ??????????????????"),
                                                fieldWithPath("[].answerId").type(JsonFieldType.NUMBER).description("??? ??????????????????"),
                                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("?????? ???????????????"),
                                                fieldWithPath("[].commentId").type(JsonFieldType.NUMBER).description("?????? ??????????????????"),
                                                fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("?????? ??? ??????????????????."),
                                                fieldWithPath("[].lastModifiedAt").type(JsonFieldType.STRING).description("?????? ?????? ??????????????????")
                                        )
                                )
                        )
                );
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ??? ????????? ????????? 409??? ????????????.")
    void deleteComment_Answer_failed_1() throws Exception {
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();
        String json = objectMapper.writeValueAsString(request);
        BDDMockito.given(commentService.findAllComments(any(),anyBoolean())).willThrow(new BusinessLogicException(ErrorCode.CANNOT_ACCESS_COMMENT));

        ResultActions perform = mockMvc.perform(get("/answers/{answer-id}/comments", dbAnswer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN)
        );
        //then 409 conflict ??????
        perform
                .andExpect(status().isConflict())
                .andDo(
                        document(
                                "?????? ???????????? ??????_??????_409",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("?????? ?????? ???????????? access token")
                                ),
                                requestFields(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????")
                                )
                        )
                );
    }
}
