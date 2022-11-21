package com.morakmorak.morak_back_end.controller.article_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.controller.ArticleController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.mapper.CategoryMapper;
import com.morakmorak.morak_back_end.mapper.FileMapper;
import com.morakmorak.morak_back_end.mapper.TagMapper;
import com.morakmorak.morak_back_end.repository.ArticleRepository;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.service.ArticleService;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.morakmorak.morak_back_end.util.ArticleTestConstants.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest({ArticleController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
@WithMockUser
class ArticleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ArticleMapper articleMapper;

    @MockBean
    ArticleRepository articleRepository;

    @MockBean
    ArticleService articleService;

    @MockBean
    TagMapper tagMapper;

    @MockBean
    FileMapper fileMapper;

    @MockBean
    CategoryMapper categoryMapper;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;

    @Test
    @DisplayName("게시글을 등록할때 등록이 완전히 성공할때")
    public void uploadArticle_suc() throws Exception {
        //given
        ArticleDto.RequestUploadArticle requestUploadArticle = REQUEST_UPLOAD_ARTICLE;
        ArticleDto.ResponseSimpleArticle responseSimpleArticle = RESPONSE_SIMPLE_ARTICLE;

        given(articleMapper.requestUploadArticleToEntity(requestUploadArticle)).willReturn(Article.builder().build());
        given(fileMapper.RequestFileWithIdToFile(requestUploadArticle))
                .willReturn(REQUEST_FILE_WITH_IDS);
        given(tagMapper.requestTagWithIdAndNameToTagDto(requestUploadArticle))
                .willReturn(REQUEST_TAG_WITH_ID_AND_NAMES);
        given(categoryMapper.RequestUploadArticleToCategory(requestUploadArticle)).willReturn(Category.builder().build());
        given(articleService.upload(any(), any(), any(), any(), any()))
                .willReturn(responseSimpleArticle);

        String content = objectMapper.writeValueAsString(requestUploadArticle);
        //when
        ResultActions perform =
                mockMvc.perform(
                        post("/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .header(JWT_HEADER, ACCESS_TOKEN)
                );

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.articleId").value(responseSimpleArticle.getArticleId()))
                .andDo(document(
                        "게시글_업로드에_성공_201",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("글의 제목입니다."),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("글의 본문입니다."),
                                        fieldWithPath("tags[].tagId").type(JsonFieldType.NUMBER).description("태그 아이디 입니다."),
                                        fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("태그 이름입니다."),
                                        fieldWithPath("category").type(JsonFieldType.STRING).description("글의 카테고리입니다."),
                                        fieldWithPath("fileId[].fileId").type(JsonFieldType.NUMBER).description("파일 아이디 입니다."),
                                        fieldWithPath("thumbnail").type(JsonFieldType.NUMBER).description("글의 썸네일 아이디 입니다.")
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("글의 아이디 입니다.")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("게시글을 등록할때 존재하지 않는 태그를 전달받아 통과에 실패할 경우 ")
    public void uploadArticle_fail_1() throws Exception {
        //given
        ArticleDto.RequestUploadArticle requestUploadArticle = REQUEST_UPLOAD_ARTICLE;
        Article article = ARTICLE;
        String content = objectMapper.writeValueAsString(requestUploadArticle);

        given(articleMapper.requestUploadArticleToEntity(requestUploadArticle)).willReturn(article);
        given(articleService.upload(any(), any(), any(), any(), any())).willThrow(new BusinessLogicException(ErrorCode.TAG_NOT_FOUND));


        //when
        ResultActions perform = mockMvc.perform(post("/articles")
                .header(JWT_HEADER, ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document(
                        "게시글을_등록할때_존재하지_않는_태그를_작성하려할떄_실패_404",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        )));
    }

    @Test
    @DisplayName("게시글을 등록할때 유효성 검증에 실패한 경우")
    public void articlUpload_fail2() throws Exception {
        //given
        ArticleDto.RequestUploadArticle requestUploadArticle = ArticleDto.RequestUploadArticle.builder().build();
        String content = objectMapper.writeValueAsString(requestUploadArticle);

        //when
        ResultActions perform =
                mockMvc.perform(
                        post("/articles")
                                .header(JWT_HEADER, ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );
        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document(
                        "게시글을_등록할때_유효성_검증에_실패한_경우_실패_400",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        )));
    }

    @Test
    @DisplayName("게시글을 수정할때 성공 테스트")
    public void articleUpdate_suc() throws Exception {
        //given
        ArticleDto.RequestUpdateArticle request = ArticleDto.RequestUpdateArticle.builder()
                .title("타이틀입니다. 잘부탁드립니다. 부탁드립니다.").content("콘텐트입니다. 잘부탁드립니다.")
                .fileId(List.of(FileDto.RequestFileWithId.builder().fileId(1L).build()))
                .tags(List.of(TagDto.SimpleTag.builder().name(TagName.JAVA).tagId(1L).build()))
                .thumbnail(1L)
                .build();
        ArticleDto.ResponseSimpleArticle response = ArticleDto.ResponseSimpleArticle.builder().articleId(1L).build();

        given(articleMapper.requestUpdateArticleToEntity(request, 1L))
                .willReturn(Article.builder().build());
        given(tagMapper.requestTagWithIdAndNameToTagDto(request))
                .willReturn(List.of(TagDto.SimpleTag.builder().build()));
        given(fileMapper.RequestFileWithIdToFile(request))
                .willReturn(List.of(FileDto.RequestFileWithId.builder().build()));
        given(articleService.update(any(), any(), any(), any()))
                .willReturn(response);

        String content = objectMapper.writeValueAsString(request);
        //when
        ResultActions perform = mockMvc.perform(
                patch("/articles/1")
                        .header(JWT_HEADER, ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(1L))
                .andDo(document(
                        "게시글_수정에_성공할_경우_성공_200",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("글의 제목입니다."),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("글의 본문입니다."),
                                        fieldWithPath("tags[].tagId").type(JsonFieldType.NUMBER).description("태그 아이디 입니다."),
                                        fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("태그 이름입니다."),
                                        fieldWithPath("fileId[].fileId").type(JsonFieldType.NUMBER).description("파일 아이디 입니다."),
                                        fieldWithPath("thumbnail").type(JsonFieldType.NUMBER).description("글의 썸네일 아이디 입니다.")
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("글의 아이디 입니다.")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("게시글을 수정할때 유효성 검증에 실패한 경우 400에러를 던진다. ")
    public void articlUpdate_fail1() throws Exception {
        //given
        ArticleDto.RequestUpdateArticle requestUploadArticle = ArticleDto.RequestUpdateArticle.builder().build();
        String content = objectMapper.writeValueAsString(requestUploadArticle);

        //when
        ResultActions perform =
                mockMvc.perform(
                        patch("/articles/1")
                                .header(JWT_HEADER, ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );
        perform.andExpect(status().isBadRequest())
                .andDo(document(
                "게시글을_수정시_유효성_검증에_실패_400",
                requestHeaders(
                        headerWithName(JWT_HEADER).description("access token")
                )
        ));
    }

    @Test
    @DisplayName("게시글을 삭제할때 성공 테스트")
    public void articleDelete_suc() throws Exception {
        //given
        Article article = Article.builder().id(1L).user(User.builder().id(1L).build()).build();
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();

        given(articleService.deleteArticle(any(),any())).willReturn(true);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/articles/1")
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );

        //then

        perform.andExpect(status().isNoContent())
                .andDo(document(
                        "게시글을_삭제할때_본인의_게시글을_삭제할때_성공_204",
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        )
                ));
    }

    @Test
    @DisplayName("게시글작성자가 아닌 유저가 해당 게시글을 삭제시 ")
    public void deleteArticle_fail1() throws Exception{
        //given
        given(articleService.deleteArticle(anyLong(), any(UserDto.UserInfo.class)))
                .willThrow(new BusinessLogicException(ErrorCode.INVALID_USER));
        //when
        ResultActions perform = mockMvc.perform(
                delete("/articles/1")
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );
        //then
        perform.andExpect(status().isUnauthorized()) .andDo(document(
                "게시글_삭제시_타인의_게시글을_삭제하려할떄_403",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(JWT_HEADER).description("access token")
                )));
     }

    @Test
    @DisplayName("게시글 삭제시 게시글이 존재하지 않을경우 404 Article_Not_Found 를 던진다. ")
    public void deleteArticle_fail2() throws Exception{
        //given
        given(articleService.deleteArticle(anyLong(), any(UserDto.UserInfo.class)))
                .willThrow(new BusinessLogicException(ErrorCode.ARTICLE_NOT_FOUND));
        //when
        ResultActions perform = mockMvc.perform(
                delete("/articles/1")
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document(
                        "게시글_삭제시_게시글이_존재하지_않을경우_실패_404",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        )));
    }

    @Test
    @DisplayName("로그인한 회원이 게시글의 좋아요를 눌렀을때 처음 좋아요를 눌렀을 경우 좋아요 갯수가 1개 증가하고 200 코드를 던진다.")
    public void pressLikeButton_suc() throws Exception{
        //given
        Long articleId = 1L;
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();

        ArticleDto.ResponseArticleLike responseArticleLike =
                ArticleDto.ResponseArticleLike.builder().articleId(1L).userId(1L).isLiked(true).likeCount(1).build();

        given(articleService.pressLikeButton(anyLong(), any())).willReturn(responseArticleLike);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/" + articleId+"/likes")
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.isLiked").value(true))
                .andExpect(jsonPath("$.likeCount").value(1))
                .andDo(document(
                        "게시글_좋아요_처음누를때_성공_200",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("글의 아이디 입니다."),
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저의 아이디 입니다."),
                                        fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("해당 유저가 좋아요를 눌렀는지 안눌렀지를 보여줍니다,"),
                                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("해당 게시글의 좋아요 숫자입니다.")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("로그인한 회원이 게시글의 누른 좋아용를 취소할 경우 좋아요 갯수가 1개 감소하고 200 코드를 던진다.")
    public void pressLikeButton_suc2() throws Exception{
        //given
        Long articleId = 1L;
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();

        ArticleDto.ResponseArticleLike responseArticleLike =
                ArticleDto.ResponseArticleLike.builder().articleId(1L).userId(1L).isLiked(true).likeCount(0).build();

        given(articleService.pressLikeButton(anyLong(), any())).willReturn(responseArticleLike);

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/" + articleId+"/likes")
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.isLiked").value(true))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andDo(document(
                        "게시글_좋아요_두번_누를때_취소_성공_200",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("글의 아이디 입니다."),
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
        Long articleId = 1L;

        given(articleService.pressLikeButton(anyLong(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/" + articleId+"/likes")
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
    @DisplayName("존재하지 않는 게시글에 좋아요를 누른다면, 404 Article Not Found 에러를 던진다.")
    public void pressLikeButton_fail2() throws Exception{
        //given
        Long articleId = 1L;

        given(articleService.pressLikeButton(anyLong(), any()))
                .willThrow(new BusinessLogicException(ErrorCode.ARTICLE_NOT_FOUND));

        //when
        ResultActions perform = mockMvc.perform(
                post("/articles/" + 500+"/likes")
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );

        //then
        perform.andExpect(status().isNotFound())
                .andDo(document(
                        "로그인한_회원이_존재하지_않는_게시글에_좋아요를_누를시_실패_404",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        )));
    }

}
