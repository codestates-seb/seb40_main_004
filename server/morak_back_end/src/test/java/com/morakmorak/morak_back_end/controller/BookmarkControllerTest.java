package com.morakmorak.morak_back_end.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.dto.BookmarkDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Bookmark;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.service.BookmarkService;
import org.junit.Before;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static com.morakmorak.morak_back_end.util.TestConstants.ID1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@Import(SecurityTestConfig.class)
@WebMvcTest({ExceptionController.class, BookmarkController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class BookmarkControllerTest {
    @MockBean
    BookmarkService bookmarkService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("북마크 등록 시 articleId, userId 중 하나라도 유효성검사에 실패하면 예외가 발생해 404를 반환한다")
    void requestPostBookmark_fail_1() throws Exception {
        //given

        Article article = Article.builder().id(3000L).build();
        User user =User.builder().id(ID1).build();
        BDDMockito.given(bookmarkService.pressBookmark(any(),anyLong())).willThrow(new BusinessLogicException(ErrorCode.ARTICLE_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/articles/{article-id}/bookmark", article.getId())
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );

        //then
        perform.andExpect(status().isNotFound())
                .andDo(
                        document("잘못된 북마크 등록 요청_실패",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("access token")
                                ),
                                pathParameters(
                                        parameterWithName("article-id").description("유효하지 않은 article Id")
                                )
                        )
                );
    }
    @Test
    @DisplayName("북마크 등록 시 올바른 userId, articleId가 들어왔다면 200 ok가 반환된다.")
    void requestPostBookmark_success_1() throws Exception {
        //given
        Article article = Article.builder().id(ID1).build();
        User user = User.builder().id(ID1).build();
        Bookmark bookmark = Bookmark.builder()
                .article(article)
                .user(user)
                .build();

        BookmarkDto.ResponsePostBookmark response = BookmarkDto.ResponsePostBookmark.builder()
                .userId(ID1)
                .articleId(ID1)
                .scrappedByThisUser(true)
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        BDDMockito.given(bookmarkService.pressBookmark(any(), anyLong())).willReturn(response);


        //when
        ResultActions perform = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/articles/{article-id}/bookmark", article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").exists())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.scrappedByThisUser").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.lastModifiedAt").exists())
                .andDo(
                        document("올바른 북마크 등록 요청_성공",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(JWT_HEADER).description("access token")
                                ),
                                pathParameters(
                                        parameterWithName("article-id").description("북마크할 게시글의 식별자")
                                ),
                                responseFields(
                                        fieldWithPath("articleId").description("게시글 식별자"),
                                        fieldWithPath("userId").description("유저 식별자"),
                                        fieldWithPath("scrappedByThisUser").description("유저에게 스크랩되었는지 여부"),
                                        fieldWithPath("createdAt").description("처음 북마크한 일자"),
                                        fieldWithPath("lastModifiedAt").description("마지막으로 메모를 수정한 일자")
                                )
                        )
                );
    }
}