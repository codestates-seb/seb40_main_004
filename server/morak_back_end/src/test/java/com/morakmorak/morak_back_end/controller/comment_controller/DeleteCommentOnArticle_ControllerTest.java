package com.morakmorak.morak_back_end.controller.comment_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.CommentController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Article;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CommentController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class DeleteCommentOnArticle_ControllerTest {
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
    }

    @Test
    @DisplayName("댓글 삭제 요청 시 적절한 요청이었다면, 삭제처리 후 200 및 댓글리스트를 반환한다.")
    void updateComment_success_1() throws Exception {
        //given 유효한 댓글 삭제 요청
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();

        CommentDto.Response deletedComment = CommentDto.Response.builder()
                .userInfo(UserDto.ResponseSimpleUserDto.of(dbComment.getUser()))
                .avatar(AvatarDto.SimpleResponse.of(dbComment.getUser().getAvatar()))
                .commentId(dbComment.getId())
                .articleId(dbComment.getArticle().getId())
                .content(dbComment.getContent())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        CommentDto.Response exampleComment = CommentDto.Response.builder()
                .userInfo(UserDto.ResponseSimpleUserDto.of(dbComment.getUser()))
                .avatar(AvatarDto.SimpleResponse.of(dbComment.getUser().getAvatar()))
                .commentId(2L)
                .articleId(dbComment.getArticle().getId())
                .content("살아남을 코멘트입니다.")
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        List<CommentDto.Response> commentList = new ArrayList<>();
        commentList.add(exampleComment);

        String json = objectMapper.writeValueAsString(request);
        BDDMockito.given(commentService.deleteComment(any(), any(), any(),anyBoolean())).willReturn(commentList);

        //when 유효한 input
        ResultActions perform = mockMvc.perform(delete("/articles/{article-id}/comments/{comment-id}", dbArticle.getId(), dbComment.getId())
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
                                                fieldWithPath("[].articleId").type(JsonFieldType.NUMBER).description("글 식별자입니다"),
                                                fieldWithPath("[].answerId").type(JsonFieldType.NULL).description("비어있는 답글 식별자입니다"),
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
    @DisplayName("댓글 삭제 요청 시 적절한 요청이었다면, 삭제처리 후 200 및 댓글리스트를 반환한다.")
    void deleteComment_success_1() throws Exception {
        //given 유효한 댓글 삭제 요청
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();

        CommentDto.Response deletedComment = CommentDto.Response.builder()
                .userInfo(UserDto.ResponseSimpleUserDto.of(dbComment.getUser()))
                .avatar(AvatarDto.SimpleResponse.of(dbComment.getUser().getAvatar()))
                .commentId(dbComment.getId())
                .articleId(dbComment.getArticle().getId())
                .content(dbComment.getContent())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        CommentDto.Response exampleComment = CommentDto.Response.builder()
                .userInfo(UserDto.ResponseSimpleUserDto.of(dbComment.getUser()))
                .avatar(AvatarDto.SimpleResponse.of(dbComment.getUser().getAvatar()))
                .commentId(2L)
                .articleId(dbComment.getArticle().getId())
                .content("살아남을 코멘트입니다.")
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        List<CommentDto.Response> commentList = new ArrayList<>();
        commentList.add(exampleComment);

        String json = objectMapper.writeValueAsString(request);
        BDDMockito.given(commentService.deleteComment(any(), any(), any(),anyBoolean())).willReturn(commentList);

        //when 유효한 input
        ResultActions perform = mockMvc.perform(delete("/articles/{article-id}/comments/{comment-id}", dbArticle.getId(), dbComment.getId())
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
                                                fieldWithPath("[].articleId").type(JsonFieldType.NUMBER).description("글 식별자입니다"),
                                                fieldWithPath("[].answerId").type(JsonFieldType.NULL).description("비어있는 답글 식별자입니다"),
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
    @DisplayName("댓글 삭제 시 삭제 권한이 없다면 409를 반환한다.")
    void deleteComment_failed_1() throws Exception {
        CommentDto.Request request = CommentDto.Request.builder()
                .content(VALID_COMMENT).build();
        String json = objectMapper.writeValueAsString(request);
        BDDMockito.given(commentService.deleteComment(any(), any(), any(),anyBoolean())).willThrow(new BusinessLogicException(ErrorCode.CANNOT_ACCESS_COMMENT));

        //when 권한 없는 유저가 삭제 요청을 보냈을 때
        ResultActions perform = mockMvc.perform(delete("/articles/{article-id}/comments/{comment-id}", dbArticle.getId(), dbComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(JWT_HEADER, ACCESS_TOKEN)
        );
        //then 409 conflict 발생
        perform
                .andExpect(status().isConflict())
                .andDo(
                        document(
                                "task delete comment failed by unauthorized user",
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
