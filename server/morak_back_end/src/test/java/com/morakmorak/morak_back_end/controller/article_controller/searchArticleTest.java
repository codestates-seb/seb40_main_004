package com.morakmorak.morak_back_end.controller.article_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.ArticleController;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.mapper.CategoryMapper;
import com.morakmorak.morak_back_end.mapper.FileMapper;
import com.morakmorak.morak_back_end.mapper.TagMapper;
import com.morakmorak.morak_back_end.repository.ArticleRepository;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.service.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static com.morakmorak.morak_back_end.util.SecurityTestConstants.ACCESS_TOKEN;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.JWT_HEADER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ArticleController.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
@WithMockUser
public class searchArticleTest {
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
    @DisplayName("게시글을 검색할때 타이틀명으로 검색에 성공할때")
    public void searchArticleTest_suc() throws Exception {
        //given
        Tag JAVA = Tag.builder().id(1L).name(TagName.JAVA).build();
        Category info = Category.builder().id(1L).name(CategoryName.INFO).build();


        Avatar dbAvatar = Avatar.builder().id(1L).remotePath("remotePath").originalFilename("fileName").build();
        User user = User.builder().id(1L).avatar(dbAvatar).nickname("nickname").grade(Grade.BRONZE).build();

        List<Answer> answers = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();

        for (int i = 1; i < 2; i++) {
            Answer answer = Answer.builder().id(1L).build();
            answers.add(answer);
            Comment comment = Comment.builder().id(1L).build();
            comments.add(comment);
        }

        ArticleTag articleTagJava = ArticleTag.builder().id(1L).tag(JAVA).build();

        List<ArticleLike> articleLikes = new ArrayList<>();

        Article article
                = Article.builder().title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~")
                .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                .articleTags(List.of(articleTagJava))
                .category(info)
                .clicks(10)
                .articleLikes(articleLikes)
                .comments(comments)
                .answers(answers)
                .user(user)
                .build();
        info.getArticleList().add(article);
        articleTagJava.injectMappingForArticleAndTag(article);

        List<Article> articles = new ArrayList<>();
        articles.add(article);

        articleLikes.add(ArticleLike.builder().article(article).user(user).build());

        //given


        List<TagDto.SimpleTag> dtoTagList = new ArrayList<>();
        TagDto.SimpleTag dtoTag = TagDto.SimpleTag.builder()
                .tagId(1L).name(TagName.JAVA).build();
        dtoTagList.add(dtoTag);


        UserDto.ResponseSimpleUserDto dtoUserInfo =
                UserDto.ResponseSimpleUserDto.builder().userId(1L)
                        .nickname("nickname").grade(Grade.BRONZE).build();

        AvatarDto.SimpleResponse dtoAvatar =
                AvatarDto.SimpleResponse.builder().avatarId(1L)
                        .remotePath("remotePath").filename("fileName").build();


        List<ArticleDto.ResponseListTypeArticle> dtoResponseListTypeArticle = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        ArticleDto.ResponseListTypeArticle responseListTypeArticleBuilder =
                ArticleDto.ResponseListTypeArticle.builder()
                        .articleId(Long.parseLong(String.valueOf(1L)))
                        .category(CategoryName.INFO)
                        .title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~")
                        .clicks(10)
                        .likes(1)
                        .answerCount(1)
                        .commentCount(1)
                        .isClosed(false)
                        .tags(dtoTagList)
                        .createdAt(now)
                        .lastModifiedAt(now)
                        .userInfo(dtoUserInfo)
                        .avatar(dtoAvatar)
                        .build();

        dtoResponseListTypeArticle.add(responseListTypeArticleBuilder);

        PageRequest pageable = PageRequest.of(0, 1, Sort.by("articleId").descending());
        Page<Article> search = new PageImpl<>(articles, pageable, 1L);
        ResponseMultiplePaging<ArticleDto.ResponseListTypeArticle> responseListTypeArticleResponseMultiplePaging =
                new ResponseMultiplePaging<>(dtoResponseListTypeArticle, search);

        given(articleService.searchArticleAsPaging("info", "테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~",
                "title", "desc", 1, 1))
                .willReturn(responseListTypeArticleResponseMultiplePaging);

//     when
        ResultActions perform = mockMvc.perform(
                get("/articles")
                        .param("category", "info")
                        .param("keyword", "테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~")
                        .param("target", "title")
                        .param("sort", "desc")
                        .param("page", "1")
                        .param("size", "1")
        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0:1].articleId").value(1))
                .andExpect(jsonPath("$.data[0:1].category").value("INFO"))
                .andExpect(jsonPath("$.data[0:1].title").value("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~"))
                .andExpect(jsonPath("$.data[0:1].clicks").value(10))
                .andExpect(jsonPath("$.data[0:1].likes").value(1))
                .andExpect(jsonPath("$.data[0:1].isClosed").value(false))
                .andExpect(jsonPath("$.data[0:1].commentCount").value(1))
                .andExpect(jsonPath("$.data[0:1].answerCount").value(1))
                .andExpect(jsonPath("$.data[0:1].createdAt").exists())
                .andExpect(jsonPath("$.data[0:1].lastModifiedAt").exists())
                .andExpect(jsonPath("$.data[0:1].tags[0:1].tagId").value(1))
                .andExpect(jsonPath("$.data[0:1].tags[0:1].name").value("JAVA"))
                .andExpect(jsonPath("$.data[0:1].userInfo.userId").value(1))
                .andExpect(jsonPath("$.data[0:1].userInfo.nickname").value("nickname"))
                .andExpect(jsonPath("$.data[0:1].userInfo.grade").value("BRONZE"))
                .andExpect(jsonPath("$.data[0:1].avatar.avatarId").value(1))
                .andExpect(jsonPath("$.data[0:1].avatar.filename").value("fileName"))
                .andExpect(jsonPath("$.data[0:1].avatar.remotePath").value("remotePath"))
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(1))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(1))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1))
                .andExpect(jsonPath("$.pageInfo.sort.sorted").value(true))
                .andDo(document(
                        "article search success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                List.of(
                                        fieldWithPath("data[].articleId").type(JsonFieldType.NUMBER).description("게시글의 아이디입니다."),
                                        fieldWithPath("data[].category").type(JsonFieldType.STRING).description("게시글의 카테고리입니다."),
                                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("게시글의 제목입니다."),
                                        fieldWithPath("data[].clicks").type(JsonFieldType.NUMBER).description("게시글의 조회수 입니다."),
                                        fieldWithPath("data[].likes").type(JsonFieldType.NUMBER).description("게시글의 좋아요 숫자입니다."),
                                        fieldWithPath("data[].isClosed").type(JsonFieldType.BOOLEAN).description("게시글의 채택 되었다면 true를 반환합니다."),
                                        fieldWithPath("data[].commentCount").type(JsonFieldType.NUMBER).description("게시글의 댓글 갯수입니다."),
                                        fieldWithPath("data[].answerCount").type(JsonFieldType.NUMBER).description("게시글의 답변 갯수입니다."),
                                        fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("게시글이 생성된 날짜입니다."),
                                        fieldWithPath("data[].lastModifiedAt").type(JsonFieldType.STRING).description("게시글이 마지막으로 수정한 날짜 입니다."),
                                        fieldWithPath("data[].tags[].tagId").type(JsonFieldType.NUMBER).description("태그 아이디 입니다."),
                                        fieldWithPath("data[].tags[].name").type(JsonFieldType.STRING).description("태그 이름입니다."),
                                        fieldWithPath("data[].userInfo.userId").type(JsonFieldType.NUMBER).description("유저의 아이디입니다."),
                                        fieldWithPath("data[].userInfo.nickname").type(JsonFieldType.STRING).description("유저의 닉네임입니다."),
                                        fieldWithPath("data[].userInfo.grade").type(JsonFieldType.STRING).description("유저의 등급입니다."),
                                        fieldWithPath("data[].avatar.avatarId").type(JsonFieldType.NUMBER).description("아바타 파일의 아이디 입니다."),
                                        fieldWithPath("data[].avatar.filename").type(JsonFieldType.STRING).description("아바타 파일의 이름입니다."),
                                        fieldWithPath("data[].avatar.remotePath").type(JsonFieldType.STRING).description("아바타 파일의 경로입니다."),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 보여질 페이지 입니다."),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("한페이지에 들어갈 게시글의 갯수 입니다."),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 게시글의 갯수 입니다."),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 숫자입니다."),
                                        fieldWithPath("pageInfo.sort.empty").type(JsonFieldType.BOOLEAN).description("페이지가 비여있다면 true를 반환합니다."),
                                        fieldWithPath("pageInfo.sort.unsorted").type(JsonFieldType.BOOLEAN).description("페이지가 정렬되지 않았다면 true를 반환합니다."),
                                        fieldWithPath("pageInfo.sort.sorted").type(JsonFieldType.BOOLEAN).description("페이지가 정렬되었다면 true를 반환합니다.")


                                )
                        )
                ));
    }

    @Test
    @DisplayName("게시글을 검색할때 태그명으로 검색에 성공할때")
    public void searchArticleTest_suc2() throws Exception {
        //given
        Tag JAVA = Tag.builder().id(1L).name(TagName.JAVA).build();
        Category info = Category.builder().id(1L).name(CategoryName.INFO).build();


        Avatar dbAvatar = Avatar.builder().id(1L).remotePath("remotePath").originalFilename("fileName").build();
        User user = User.builder().id(1L).avatar(dbAvatar).nickname("nickname").grade(Grade.BRONZE).build();

        List<Answer> answers = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();

        for (int i = 1; i < 2; i++) {
            Answer answer = Answer.builder().id(1L).build();
            answers.add(answer);
            Comment comment = Comment.builder().id(1L).build();
            comments.add(comment);

        }

        ArticleTag articleTagJava = ArticleTag.builder().id(1L).tag(JAVA).build();

        List<ArticleLike> articleLikes = new ArrayList<>();

        Article article
                = Article.builder().title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~")
                .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                .articleTags(List.of(articleTagJava))
                .category(info)
                .clicks(10)
                .articleLikes(articleLikes)
                .comments(comments)
                .answers(answers)
                .user(user)
                .build();
        info.getArticleList().add(article);
        articleTagJava.injectMappingForArticleAndTag(article);

        List<Article> articles = new ArrayList<>();
        articles.add(article);

        articleLikes.add(ArticleLike.builder().article(article).user(user).build());


        //given


        List<TagDto.SimpleTag> dtoTagList = new ArrayList<>();
        TagDto.SimpleTag dtoTag = TagDto.SimpleTag.builder()
                .tagId(1L).name(TagName.JAVA).build();
        dtoTagList.add(dtoTag);


        UserDto.ResponseSimpleUserDto dtoUserInfo =
                UserDto.ResponseSimpleUserDto.builder().userId(1L)
                        .nickname("nickname").grade(Grade.BRONZE).build();

        AvatarDto.SimpleResponse dtoAvatar =
                AvatarDto.SimpleResponse.builder().avatarId(1L)
                        .remotePath("remotePath").filename("fileName").build();


        List<ArticleDto.ResponseListTypeArticle> dtoResponseListTypeArticle = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        ArticleDto.ResponseListTypeArticle responseListTypeArticleBuilder =
                ArticleDto.ResponseListTypeArticle.builder()
                        .articleId(Long.parseLong(String.valueOf(1L)))
                        .category(CategoryName.INFO)
                        .title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~")
                        .clicks(10)
                        .likes(1)
                        .answerCount(1)
                        .commentCount(1)
                        .isClosed(false)
                        .tags(dtoTagList)
                        .createdAt(now)
                        .lastModifiedAt(now)
                        .userInfo(dtoUserInfo)
                        .avatar(dtoAvatar)
                        .build();

        dtoResponseListTypeArticle.add(responseListTypeArticleBuilder);

        PageRequest pageable = PageRequest.of(0, 1, Sort.by("articleId").descending());
        Page<Article> search = new PageImpl<>(articles, pageable, 1L);
        ResponseMultiplePaging<ArticleDto.ResponseListTypeArticle> responseListTypeArticleResponseMultiplePaging =
                new ResponseMultiplePaging<>(dtoResponseListTypeArticle, search);

        given(articleService.searchArticleAsPaging("INFO", "JAVA",
                "tag", "desc", 1, 1))
                .willReturn(responseListTypeArticleResponseMultiplePaging);

//     when
        ResultActions perform = mockMvc.perform(
                get("/articles")
                        .param("category", "INFO")
                        .param("keyword", "JAVA")
                        .param("target", "tag")
                        .param("sort", "desc")
                        .param("page", "1")
                        .param("size", "1")
        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0:1].articleId").value(1))
                .andExpect(jsonPath("$.data[0:1].category").value("INFO"))
                .andExpect(jsonPath("$.data[0:1].title").value("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~"))
                .andExpect(jsonPath("$.data[0:1].clicks").value(10))
                .andExpect(jsonPath("$.data[0:1].likes").value(1))
                .andExpect(jsonPath("$.data[0:1].isClosed").value(false))
                .andExpect(jsonPath("$.data[0:1].commentCount").value(1))
                .andExpect(jsonPath("$.data[0:1].answerCount").value(1))
                .andExpect(jsonPath("$.data[0:1].createdAt").exists())
                .andExpect(jsonPath("$.data[0:1].lastModifiedAt").exists())
                .andExpect(jsonPath("$.data[0:1].tags[0:1].tagId").value(1))
                .andExpect(jsonPath("$.data[0:1].tags[0:1].name").value("JAVA"))
                .andExpect(jsonPath("$.data[0:1].userInfo.userId").value(1))
                .andExpect(jsonPath("$.data[0:1].userInfo.nickname").value("nickname"))
                .andExpect(jsonPath("$.data[0:1].userInfo.grade").value("BRONZE"))
                .andExpect(jsonPath("$.data[0:1].avatar.avatarId").value(1))
                .andExpect(jsonPath("$.data[0:1].avatar.filename").value("fileName"))
                .andExpect(jsonPath("$.data[0:1].avatar.remotePath").value("remotePath"))
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(1))
                .andExpect(jsonPath("$.pageInfo.totalElements").value(1))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1))
                .andExpect(jsonPath("$.pageInfo.sort.sorted").value(true))
                .andDo(document(
                        "article search success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                List.of(
                                        fieldWithPath("data[].articleId").type(JsonFieldType.NUMBER).description("게시글의 아이디입니다."),
                                        fieldWithPath("data[].category").type(JsonFieldType.STRING).description("게시글의 카테고리입니다."),
                                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("게시글의 제목입니다."),
                                        fieldWithPath("data[].clicks").type(JsonFieldType.NUMBER).description("게시글의 조회수 입니다."),
                                        fieldWithPath("data[].likes").type(JsonFieldType.NUMBER).description("게시글의 좋아요 숫자입니다."),
                                        fieldWithPath("data[].isClosed").type(JsonFieldType.BOOLEAN).description("게시글의 채택 되었다면 true를 반환합니다."),
                                        fieldWithPath("data[].commentCount").type(JsonFieldType.NUMBER).description("게시글의 댓글 갯수입니다."),
                                        fieldWithPath("data[].answerCount").type(JsonFieldType.NUMBER).description("게시글의 답변 갯수입니다."),
                                        fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("게시글이 생성된 날짜입니다."),
                                        fieldWithPath("data[].lastModifiedAt").type(JsonFieldType.STRING).description("게시글이 마지막으로 수정한 날짜 입니다."),
                                        fieldWithPath("data[].tags[].tagId").type(JsonFieldType.NUMBER).description("태그 아이디 입니다."),
                                        fieldWithPath("data[].tags[].name").type(JsonFieldType.STRING).description("태그 이름입니다."),
                                        fieldWithPath("data[].userInfo.userId").type(JsonFieldType.NUMBER).description("유저의 아이디입니다."),
                                        fieldWithPath("data[].userInfo.nickname").type(JsonFieldType.STRING).description("유저의 닉네임입니다."),
                                        fieldWithPath("data[].userInfo.grade").type(JsonFieldType.STRING).description("유저의 등급입니다."),
                                        fieldWithPath("data[].avatar.avatarId").type(JsonFieldType.NUMBER).description("아바타 파일의 아이디 입니다."),
                                        fieldWithPath("data[].avatar.filename").type(JsonFieldType.STRING).description("아바타 파일의 이름입니다."),
                                        fieldWithPath("data[].avatar.remotePath").type(JsonFieldType.STRING).description("아바타 파일의 경로입니다."),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 보여질 페이지 입니다."),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("한페이지에 들어갈 게시글의 갯수 입니다."),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 게시글의 갯수 입니다."),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 숫자입니다."),
                                        fieldWithPath("pageInfo.sort.empty").type(JsonFieldType.BOOLEAN).description("페이지가 비여있다면 true를 반환합니다."),
                                        fieldWithPath("pageInfo.sort.unsorted").type(JsonFieldType.BOOLEAN).description("페이지가 정렬되지 않았다면 true를 반환합니다."),
                                        fieldWithPath("pageInfo.sort.sorted").type(JsonFieldType.BOOLEAN).description("페이지가 정렬되었다면 true를 반환합니다.")


                                )
                        )
                ));

    }

    @Test
    @DisplayName("게시글 상세조회 API를 이용할때 사용자가 좋아요와 북마크를 했다면 201 코드를 던지고 ResponseDetailArticle DTO를 반환한다.")
    public void findDetailArticle_suc() throws Exception {
        //given
        Tag JAVA = Tag.builder().id(1L).name(TagName.JAVA).build();
        Category info = Category.builder().id(1L).name(CategoryName.INFO).build();

        Avatar dbAvatar = Avatar.builder().id(1L).remotePath("remotePath").originalFilename("fileName").build();
        User user = User.builder().id(1L).avatar(dbAvatar).nickname("nickname").grade(Grade.BRONZE).build();

        List<Comment> comments = new ArrayList<>();
        List<Answer> answers = new ArrayList<>();

        for (int i = 1; i < 2; i++) {
            Answer answer = Answer.builder().id(1L).build();
            answers.add(answer);
            Comment comment = Comment.builder().id(1L).build();
            comments.add(comment);
        }

        ArticleTag articleTagJava = ArticleTag.builder().id(1L).tag(JAVA).build();

        List<ArticleLike> articleLikes = new ArrayList<>();

        Article article
                = Article.builder()
                .id(1L)
                .title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~")
                .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                .articleTags(List.of(articleTagJava))
                .category(info)
                .clicks(10)
                .isClosed(false)
                .answers(answers)
                .articleLikes(articleLikes)
                .comments(comments)
                .user(user)
                .build();

        info.getArticleList().add(article);

        articleTagJava.injectMappingForArticleAndTag(article);

        List<Article> articles = new ArrayList<>();
        articles.add(article);

        articleLikes.add(ArticleLike.builder().id(1L).article(article).user(user).build());

        UserDto.UserInfo userInfo = UserDto.UserInfo.builder().id(1L).build();

        AvatarDto.SimpleResponse avatarDto = AvatarDto.SimpleResponse.builder()
                .avatarId(1L).filename("fileName").remotePath("remotePath").build();

        UserDto.ResponseSimpleUserDto userInfoDto = UserDto.ResponseSimpleUserDto.builder()
                .userId(1L).nickname("nickname").grade(Grade.BRONZE).build();

        CommentDto.Response commentDto = CommentDto.Response.builder()
                .commentId(1L)
                .articleId(1L)
                .content("comment 입니다.")
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .userInfo(userInfoDto)
                .avatar(avatarDto)
                .build();

        TagDto.SimpleTag tagDto =
                TagDto.SimpleTag.builder().tagId(1L).name(TagName.JAVA).build();

        ArticleDto.ResponseDetailArticle result = ArticleDto.ResponseDetailArticle.builder()
                .articleId(1L)
                .category(CategoryName.INFO)
                .title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~")
                .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                .clicks(10)
                .likes(1)
                .isClosed(false)
                .isBookmarked(true)
                .isLiked(true)
                .tags(List.of(tagDto))
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .expiredDate(null)
                .userInfo(userInfoDto)
                .avatar(avatarDto)
                .comments(List.of(commentDto))
                .build();

        given(articleService.findDetailArticle(anyLong(), any())).willReturn(result);

        //when
        ResultActions perform = mockMvc.perform(
                get("/articles/" + article.getId())
                        .header(JWT_HEADER, ACCESS_TOKEN)
        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(article.getId()))
                .andExpect(jsonPath("$.category").value("INFO"))
                .andExpect(jsonPath("$.title").value("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~"))
                .andExpect(jsonPath("$.content").value("콘탠트입니다. 제발 됬으면 좋겠습니다."))
                .andExpect(jsonPath("$.clicks").value(10))
                .andExpect(jsonPath("$.likes").value(1))
                .andExpect(jsonPath("$.isClosed").value(false))
                .andExpect(jsonPath("$.isLiked").value(true))
                .andExpect(jsonPath("$.isBookmarked").value(true))
                .andExpect(jsonPath("$.tags[0:1].tagId").value(1))
                .andExpect(jsonPath("$.tags[0:1].name").value("JAVA"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty())
                .andExpect(jsonPath("$.expiredDate").isEmpty())
                .andExpect(jsonPath("$.userInfo.userId").value(1))
                .andExpect(jsonPath("$.userInfo.nickname").value("nickname"))
                .andExpect(jsonPath("$.userInfo.grade").value("BRONZE"))
                .andExpect(jsonPath("$.avatar.avatarId").value(1))
                .andExpect(jsonPath("$.avatar.filename").value("fileName"))
                .andExpect(jsonPath("$.avatar.remotePath").value("remotePath"))
                .andExpect(jsonPath("$.comments[0:1].articleId").value(1))
                .andExpect(jsonPath("$.comments[0:1].commentId").value(1))
                .andExpect(jsonPath("$.comments[0:1].userInfo.userId").value(1))
                .andExpect(jsonPath("$.comments[0:1].userInfo.nickname").value("nickname"))
                .andExpect(jsonPath("$.comments[0:1].userInfo.grade").value("BRONZE"))
                .andExpect(jsonPath("$.comments[0:1].avatar.avatarId").value(1))
                .andExpect(jsonPath("$.comments[0:1].avatar.filename").value("fileName"))
                .andExpect(jsonPath("$.comments[0:1].avatar.remotePath").value("remotePath"))
                .andExpect(jsonPath("$.comments[0:1].createdAt").exists())
                .andExpect(jsonPath("$.comments[0:1].lastModifiedAt").exists())

                .andDo(document(
                        "article search success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(JWT_HEADER).description("access tokend이 필요없을때도 있음")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("게시글의 아이디입니다."),
                                        fieldWithPath("category").type(JsonFieldType.STRING).description("게시글의 카테고리입니다."),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시글의 제목입니다."),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시글의 내용입니."),
                                        fieldWithPath("clicks").type(JsonFieldType.NUMBER).description("게시글의 조회수 입니다."),
                                        fieldWithPath("likes").type(JsonFieldType.NUMBER).description("게시글의 좋아요 숫자입니다."),
                                        fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("jwt의 회원이 좋아요를 누르면 true를 반환합니다."),
                                        fieldWithPath("isBookmarked").type(JsonFieldType.BOOLEAN).description("jwt의 회원이 북마크 했다면 true를 반환합니다."),
                                        fieldWithPath("isClosed").type(JsonFieldType.BOOLEAN).description("게시글의 채택 되었다면 true를 반환합니다."),
                                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시글이 생성된 날짜입니다."),
                                        fieldWithPath("lastModifiedAt").type(JsonFieldType.STRING).description("게시글이 마지막으로 수정한 날짜 입니다."),
                                        fieldWithPath("expiredDate").type(JsonFieldType.NULL).description("게시글이 유효기한입니다. 아직은 사용하지 않습니다."),
                                        fieldWithPath("tags[].tagId").type(JsonFieldType.NUMBER).description("태그 아이디 입니다."),
                                        fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("태그 이름입니다."),
                                        fieldWithPath("userInfo.userId").type(JsonFieldType.NUMBER).description("유저의 아이디입니다."),
                                        fieldWithPath("userInfo.nickname").type(JsonFieldType.STRING).description("유저의 닉네임입니다."),
                                        fieldWithPath("userInfo.grade").type(JsonFieldType.STRING).description("유저의 등급입니다."),
                                        fieldWithPath("avatar.avatarId").type(JsonFieldType.NUMBER).description("아바타 파일의 아이디 입니다."),
                                        fieldWithPath("avatar.filename").type(JsonFieldType.STRING).description("아바타 파일의 이름입니다."),
                                        fieldWithPath("avatar.remotePath").type(JsonFieldType.STRING).description("아바타 파일의 경로입니다."),
                                        fieldWithPath("comments[].commentId").type(JsonFieldType.NUMBER).description("댓글의 아이디 입니다.."),
                                        fieldWithPath("comments[].articleId").type(JsonFieldType.NUMBER).description("댓글이 올라가있는 게시글의 아이디 입니다.."),
                                        fieldWithPath("comments[].content").type(JsonFieldType.STRING).description("댓글의 내용입니다."),
                                        fieldWithPath("comments[].createdAt").type(JsonFieldType.STRING).description("댓글이 작성된 날짜 입니다."),
                                        fieldWithPath("comments[].lastModifiedAt").type(JsonFieldType.STRING).description("댓글이 마지막으로 수정된 날짜 입니다."),
                                        fieldWithPath("comments[].userInfo.userId").type(JsonFieldType.NUMBER).description("댓글 유저의 아이디입니다."),
                                        fieldWithPath("comments[].userInfo.nickname").type(JsonFieldType.STRING).description("댓글 유저의 닉네임입니다."),
                                        fieldWithPath("comments[].userInfo.grade").type(JsonFieldType.STRING).description("댓글 유저의 등급입니다."),
                                        fieldWithPath("comments[].avatar.avatarId").type(JsonFieldType.NUMBER).description("댓글 유저의 아바타 파일의 아이디 입니다."),
                                        fieldWithPath("comments[].avatar.filename").type(JsonFieldType.STRING).description("댓글 유저의 아바타 파일의 이름입니다."),
                                        fieldWithPath("comments[].avatar.remotePath").type(JsonFieldType.STRING).description("댓글 유저의 아바타 파일의 경로입니다.")
                                ))));

    }

    @Test
    @DisplayName("dsfds")
    public void findDetailArticle_suc2() throws Exception {
        //given
        Tag JAVA = Tag.builder().id(1L).name(TagName.JAVA).build();
        Category info = Category.builder().id(1L).name(CategoryName.INFO).build();

        Avatar dbAvatar = Avatar.builder().id(1L).remotePath("remotePath").originalFilename("fileName").build();
        User user = User.builder().id(1L).avatar(dbAvatar).nickname("nickname").grade(Grade.BRONZE).build();

        List<Comment> comments = new ArrayList<>();
        List<Answer> answers = new ArrayList<>();

        for (int i = 1; i < 2; i++) {
            Answer answer = Answer.builder().id(1L).build();
            answers.add(answer);
            Comment comment = Comment.builder().id(1L).build();
            comments.add(comment);
        }

        ArticleTag articleTagJava = ArticleTag.builder().id(1L).tag(JAVA).build();

        List<ArticleLike> articleLikes = new ArrayList<>();

        Article article
                = Article.builder()
                .id(1L)
                .title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~")
                .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                .articleTags(List.of(articleTagJava))
                .category(info)
                .clicks(10)
                .isClosed(false)
                .answers(answers)
                .articleLikes(articleLikes)
                .comments(comments)
                .user(user)
                .build();

        info.getArticleList().add(article);

        articleTagJava.injectMappingForArticleAndTag(article);

        List<Article> articles = new ArrayList<>();
        articles.add(article);

        articleLikes.add(ArticleLike.builder().id(1L).article(article).user(user).build());


        AvatarDto.SimpleResponse avatarDto = AvatarDto.SimpleResponse.builder()
                .avatarId(1L).filename("fileName").remotePath("remotePath").build();

        UserDto.ResponseSimpleUserDto userInfoDto = UserDto.ResponseSimpleUserDto.builder()
                .userId(1L).nickname("nickname").grade(Grade.BRONZE).build();

        CommentDto.Response commentDto = CommentDto.Response.builder()
                .commentId(1L)
                .articleId(1L)
                .content("comment 입니다.")
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .userInfo(userInfoDto)
                .avatar(avatarDto)
                .build();

        TagDto.SimpleTag tagDto =
                TagDto.SimpleTag.builder().tagId(1L).name(TagName.JAVA).build();

        ArticleDto.ResponseDetailArticle result = ArticleDto.ResponseDetailArticle.builder()
                .articleId(1L)
                .category(CategoryName.INFO)
                .title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~")
                .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                .clicks(10)
                .likes(1)
                .isClosed(false)
                .isBookmarked(false)
                .isLiked(false)
                .tags(List.of(tagDto))
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .userInfo(userInfoDto)
                .avatar(avatarDto)
                .comments(List.of(commentDto))
                .build();

        given(articleService.findDetailArticle(anyLong(), any())).willReturn(result);

        //when
        ResultActions perform = mockMvc.perform(
                get("/articles/" + article.getId())

        );

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(article.getId()))
                .andExpect(jsonPath("$.category").value("INFO"))
                .andExpect(jsonPath("$.title").value("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~"))
                .andExpect(jsonPath("$.content").value("콘탠트입니다. 제발 됬으면 좋겠습니다."))
                .andExpect(jsonPath("$.clicks").value(10))
                .andExpect(jsonPath("$.likes").value(1))
                .andExpect(jsonPath("$.isClosed").value(false))
                .andExpect(jsonPath("$.isLiked").value(false))
                .andExpect(jsonPath("$.isBookmarked").value(false))
                .andExpect(jsonPath("$.tags[0:1].tagId").value(1))
                .andExpect(jsonPath("$.tags[0:1].name").value("JAVA"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty())
                .andExpect(jsonPath("$.expiredDate").isEmpty())
                .andExpect(jsonPath("$.userInfo.userId").value(1))
                .andExpect(jsonPath("$.userInfo.nickname").value("nickname"))
                .andExpect(jsonPath("$.userInfo.grade").value("BRONZE"))
                .andExpect(jsonPath("$.avatar.avatarId").value(1))
                .andExpect(jsonPath("$.avatar.filename").value("fileName"))
                .andExpect(jsonPath("$.avatar.remotePath").value("remotePath"))
                .andExpect(jsonPath("$.comments[0:1].articleId").value(1))
                .andExpect(jsonPath("$.comments[0:1].commentId").value(1))
                .andExpect(jsonPath("$.comments[0:1].userInfo.userId").value(1))
                .andExpect(jsonPath("$.comments[0:1].userInfo.nickname").value("nickname"))
                .andExpect(jsonPath("$.comments[0:1].userInfo.grade").value("BRONZE"))
                .andExpect(jsonPath("$.comments[0:1].avatar.avatarId").value(1))
                .andExpect(jsonPath("$.comments[0:1].avatar.filename").value("fileName"))
                .andExpect(jsonPath("$.comments[0:1].avatar.remotePath").value("remotePath"))
                .andExpect(jsonPath("$.comments[0:1].createdAt").exists())
                .andExpect(jsonPath("$.comments[0:1].lastModifiedAt").exists())
                .andDo(document(
                        "article search success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                List.of(
                                        fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("게시글의 아이디입니다."),
                                        fieldWithPath("category").type(JsonFieldType.STRING).description("게시글의 카테고리입니다."),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시글의 제목입니다."),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시글의 내용입니."),
                                        fieldWithPath("clicks").type(JsonFieldType.NUMBER).description("게시글의 조회수 입니다."),
                                        fieldWithPath("likes").type(JsonFieldType.NUMBER).description("게시글의 좋아요 숫자입니다."),
                                        fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("jwt의 회원이 좋아요를 누르면 true를 반환합니다."),
                                        fieldWithPath("isBookmarked").type(JsonFieldType.BOOLEAN).description("jwt의 회원이 북마크 했다면 true를 반환합니다."),
                                        fieldWithPath("isClosed").type(JsonFieldType.BOOLEAN).description("게시글의 채택 되었다면 true를 반환합니다."),
                                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시글이 생성된 날짜입니다."),
                                        fieldWithPath("lastModifiedAt").type(JsonFieldType.STRING).description("게시글이 마지막으로 수정한 날짜 입니다."),
                                        fieldWithPath("expiredDate").type(JsonFieldType.NULL).description("게시글이 유효기한입니다. 아직은 사용하지 않습니다."),
                                        fieldWithPath("tags[].tagId").type(JsonFieldType.NUMBER).description("태그 아이디 입니다."),
                                        fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("태그 이름입니다."),
                                        fieldWithPath("userInfo.userId").type(JsonFieldType.NUMBER).description("유저의 아이디입니다."),
                                        fieldWithPath("userInfo.nickname").type(JsonFieldType.STRING).description("유저의 닉네임입니다."),
                                        fieldWithPath("userInfo.grade").type(JsonFieldType.STRING).description("유저의 등급입니다."),
                                        fieldWithPath("avatar.avatarId").type(JsonFieldType.NUMBER).description("아바타 파일의 아이디 입니다."),
                                        fieldWithPath("avatar.filename").type(JsonFieldType.STRING).description("아바타 파일의 이름입니다."),
                                        fieldWithPath("avatar.remotePath").type(JsonFieldType.STRING).description("아바타 파일의 경로입니다."),
                                        fieldWithPath("comments[].commentId").type(JsonFieldType.NUMBER).description("댓글의 아이디 입니다.."),
                                        fieldWithPath("comments[].articleId").type(JsonFieldType.NUMBER).description("댓글이 올라가있는 게시글의 아이디 입니다.."),
                                        fieldWithPath("comments[].content").type(JsonFieldType.STRING).description("댓글의 내용입니다."),
                                        fieldWithPath("comments[].createdAt").type(JsonFieldType.STRING).description("댓글이 작성된 날짜 입니다."),
                                        fieldWithPath("comments[].lastModifiedAt").type(JsonFieldType.STRING).description("댓글이 마지막으로 수정된 날짜 입니다."),
                                        fieldWithPath("comments[].userInfo.userId").type(JsonFieldType.NUMBER).description("댓글 유저의 아이디입니다."),
                                        fieldWithPath("comments[].userInfo.nickname").type(JsonFieldType.STRING).description("댓글 유저의 닉네임입니다."),
                                        fieldWithPath("comments[].userInfo.grade").type(JsonFieldType.STRING).description("댓글 유저의 등급입니다."),
                                        fieldWithPath("comments[].avatar.avatarId").type(JsonFieldType.NUMBER).description("댓글 유저의 아바타 파일의 아이디 입니다."),
                                        fieldWithPath("comments[].avatar.filename").type(JsonFieldType.STRING).description("댓글 유저의 아바타 파일의 이름입니다."),
                                        fieldWithPath("comments[].avatar.remotePath").type(JsonFieldType.STRING).description("댓글 유저의 아바타 파일의 경로입니다.")
                                ))));
    }

}

