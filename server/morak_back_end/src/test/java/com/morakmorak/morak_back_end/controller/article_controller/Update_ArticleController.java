package com.morakmorak.morak_back_end.controller.article_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.ArticleController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.dto.FileDto;
import com.morakmorak.morak_back_end.dto.TagDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.mapper.CategoryMapper;
import com.morakmorak.morak_back_end.mapper.FileMapper;
import com.morakmorak.morak_back_end.mapper.TagMapper;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.service.ArticleService;
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

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest({ArticleController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
@WithMockUser
class Update_ArticleController {

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
                patch("/articles/{article-id}",1)
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
                        pathParameters(
                                parameterWithName("article-id").description("게시글의 아이디 입니다.")
                        ),
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
    public void articleUpdate_fail1() throws Exception {
        //given
        ArticleDto.RequestUpdateArticle requestUploadArticle = ArticleDto.RequestUpdateArticle.builder()
                .title("타이틀").content("콘텐트")
                .fileId(List.of(FileDto.RequestFileWithId.builder().fileId(1L).build()))
                .tags(List.of(TagDto.SimpleTag.builder().name(TagName.JAVA).tagId(1L).build()))
                .thumbnail(1L)
                .build();
        String content = objectMapper.writeValueAsString(requestUploadArticle);

        //when
        ResultActions perform =
                mockMvc.perform(
                        patch("/articles/{article-id}",1)
                                .header(JWT_HEADER, ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );
        perform.andExpect(status().isBadRequest())
                .andDo(document(
                        "게시글을_수정시_유효성_검증에_실패_400",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("article-id").description("게시글의 아이디 입니다.")
                        ),
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
                        )));
    }
    @Test
    @DisplayName("게시글을 수정할때 이미 삭제된 게시글이라면 403에러를 던진다.")
    public void articleUpdate_fail2() throws Exception {
        //given
        ArticleDto.RequestUpdateArticle requestUploadArticle = ArticleDto.RequestUpdateArticle.builder()
                .title("타이틀을 적어볼까요호호").content("콘텐트입니다 콘텐트")
                .fileId(List.of(FileDto.RequestFileWithId.builder().fileId(1L).build()))
                .tags(List.of(TagDto.SimpleTag.builder().name(TagName.JAVA).tagId(1L).build()))
                .thumbnail(1L)
                .build();

        String content = objectMapper.writeValueAsString(requestUploadArticle);
        given(articleService.update(any(), any(), any(), any())).willThrow(new BusinessLogicException(ErrorCode.NO_ACCESS_TO_THAT_OBJECT));

        //when
        ResultActions perform =
                mockMvc.perform(
                        patch("/articles/{article-id}",1)
                                .header(JWT_HEADER, ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );
        perform.andExpect(status().isForbidden())
                .andDo(document(
                        "게시글을_수정시_이미 삭제된 게시물인 경우_403",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("article-id").description("게시글의 아이디 입니다.")
                        ),
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
                        )));
    }
}