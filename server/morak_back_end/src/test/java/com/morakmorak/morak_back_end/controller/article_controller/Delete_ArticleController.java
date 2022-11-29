package com.morakmorak.morak_back_end.controller.article_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.ArticleController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.User;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ArticleController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
@WithMockUser
public class Delete_ArticleController {
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
    @DisplayName("게시글을 삭제할때 성공 테스트")
    public void articleDelete_suc() throws Exception {
        //given
        Article article = Article.builder().id(1L).user(User.builder().id(1L).build()).build();
        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();

        given(articleService.deleteArticle(any(),any())).willReturn(true);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/articles/{article-id}",1)
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );

        //then

        perform.andExpect(status().isNoContent())
                .andDo(document(
                        "게시글을_삭제할때_본인의_게시글을_삭제할때_성공_204",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("article-id").description("게시글의 아이디 입니다.")
                        ),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        )
                ));
    }

    @Test
    @DisplayName("게시글작성자가 아닌 유저가 해당 게시글을 삭제시 403 반환")
    public void deleteArticle_fail1() throws Exception{
        //given
        given(articleService.deleteArticle(anyLong(), any(UserDto.UserInfo.class)))
                .willThrow(new BusinessLogicException(ErrorCode.INVALID_USER));
        //when
        ResultActions perform = mockMvc.perform(
                delete("/articles/{article-id}",1)
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );
        //then
        perform.andExpect(status().isUnauthorized()) .andDo(document(
                "게시글_삭제시_타인의_게시글을_삭제하려할떄_403",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName("article-id").description("게시글의 아이디 입니다.")
                ),
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
                delete("/articles/{article-id}",1)
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document(
                        "게시글_삭제시_게시글이_존재하지_않을경우_실패_404",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("article-id").description("게시글의 아이디 입니다.")
                        ),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        )));
    }

    @Test
    @DisplayName("게시글 삭제 시 이미 삭제 상태인 경우 403를 던진다.")
    public void deleteArticle_fail3() throws Exception{
        //given
        given(articleService.deleteArticle(anyLong(), any(UserDto.UserInfo.class)))
                .willThrow(new BusinessLogicException(ErrorCode.NO_ACCESS_TO_THAT_OBJECT));
        //when
        ResultActions perform = mockMvc.perform(
                delete("/articles/{article-id}",1)
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );
        //then
        perform.andExpect(status().isForbidden())
                .andDo(document(
                        "게시글_삭제시_게시글이_존재하지_않을경우_실패_404",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("article-id").description("게시글의 아이디 입니다.")
                        ),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access token")
                        )));
    }
}

