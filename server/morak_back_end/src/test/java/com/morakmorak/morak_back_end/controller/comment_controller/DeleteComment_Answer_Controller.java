package com.morakmorak.morak_back_end.controller.comment_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.CommentController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.*;
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
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CommentController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class DeleteComment_Answer_Controller {
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
    Article dbArticle;
    Comment dbComment;
    Comment dbComment1;

    @BeforeEach
    public void setUp() throws Exception {
        String VALID_CONTENT = "유효한 댓글내용입니다.";

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

        this.dbArticle = Article.builder()
                .id(ID1)
                .user(dbUser)
                .build();

        this.dbComment = Comment.builder()
                .id(ID1)
                .user(dbUser)
                .article(dbArticle)
                .content(VALID_CONTENT)
                .build();

        this.dbComment1 = Comment.builder()
                .id(ID2)
                .user(dbUser)
                .answer(Answer.builder().id(1L).build())
                .content(VALID_CONTENT)
                .build();
    }

    @Test
    @DisplayName("답변에 달린 댓글 삭제 요청 시 적절한 요청이었다면, 삭제처리 후 200 및 댓글리스트를 반환한다.")
    void deleteComment_Answer_success_1() throws Exception {
        //given 유효한 댓글 삭제 요청
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();


        CommentDto.ResponseForAnswer deletedComment = CommentDto.ResponseForAnswer.builder()
                .userInfo(UserDto.ResponseSimpleUserDto.of(dbComment1.getUser()))
                .avatar(AvatarDto.SimpleResponse.of(dbComment1.getUser().getAvatar()))
                .commentId(dbComment1.getId())
                .answerId(dbComment1.getAnswer().getId())
                .content(dbComment1.getContent())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        CommentDto.ResponseForAnswer exampleComment = CommentDto.ResponseForAnswer.builder()
                .userInfo(UserDto.ResponseSimpleUserDto.of(dbComment.getUser()))
                .avatar(AvatarDto.SimpleResponse.of(dbComment.getUser().getAvatar()))
                .commentId(2L)
                .answerId(3L)
                .content("살아남을 코멘트입니다.")
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        Answer answer = Answer.builder().id(1L).build();
        Comment comment = Comment.builder().id(1L).answer(answer).build();

        List<CommentDto.ResponseForAnswer> commentList = new ArrayList<>();
        commentList.add(exampleComment);

        String json = objectMapper.writeValueAsString(request);
        BDDMockito.given(commentService.deleteCommentOnAnswer(any(),any(),any())).willReturn(commentList);

        //when 유효한 input
        ResultActions perform = mockMvc.perform(delete("/answers/{answer-id}/comments/{comment-id}", answer.getId(), comment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN)
        );
        perform.andExpect(status().isOk())
                .andDo(
                        document(
                                "delete comment succeeded",
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

}
