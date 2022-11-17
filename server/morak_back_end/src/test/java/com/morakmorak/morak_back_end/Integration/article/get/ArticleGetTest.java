package com.morakmorak.morak_back_end.Integration.article.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.repository.*;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import com.morakmorak.morak_back_end.service.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
public class ArticleGetTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @PersistenceContext
    EntityManager em;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RedisRepository<String> mailAuthRedisRepository;

    @Autowired
    ArticleService articleService;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ArticleTagRepository articleTagRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;


    @Test
    @DisplayName("게시글을 타이틀명과 카테고리로 검색 성공시 201코드와 Ok를 반환한다.")
    public void searchArticleTest_title() throws Exception {
        Tag JAVA = Tag.builder().name(TagName.JAVA).build();
        em.persist(JAVA);

        Category info = Category.builder().name("info").build();
        em.persist(info);

        Avatar avatar = Avatar.builder().remotePath("remotePath")
                .originalFileName("fileName")
                .build();
        em.persist(avatar);

        User user = User.builder().nickname("nickname").grade(Grade.BRONZE).build();

        List<ArticleLike> articleLikes = new ArrayList<>();

        ArticleTag articleTagJava = ArticleTag.builder().tag(JAVA).build();

        Article article = Article.builder().title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~")
                .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                .articleTags(List.of(articleTagJava))
                .category(info)
                .articleLikes(articleLikes)
                .user(user)
                .build();
        info.getArticleList().add(article);
        articleTagJava.injectMappingForArticleAndTag(article);
        em.persist(article);

        user.getArticles().add(article);
        em.persist(user);

        //when
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
        perform.andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글을 태그명과 카테고리로 검색 성공시 201코드와 Ok를 반환한다.")
    public void searchArticleTest_tag() throws Exception {
        Tag JAVA = Tag.builder().name(TagName.JAVA).build();
        em.persist(JAVA);

        Category info = Category.builder().name("info").build();
        em.persist(info);

        Avatar avatar = Avatar.builder().remotePath("remotePath")
                .originalFileName("fileName")
                .build();
        em.persist(avatar);

        User user = User.builder().nickname("nickname").grade(Grade.BRONZE).build();

        List<ArticleLike> articleLikes = new ArrayList<>();

        ArticleTag articleTagJava = ArticleTag.builder().tag(JAVA).build();

        Article article = Article.builder().title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~")
                .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                .articleTags(List.of(articleTagJava))
                .category(info)
                .articleLikes(articleLikes)
                .user(user)
                .build();
        info.getArticleList().add(article);
        articleTagJava.injectMappingForArticleAndTag(article);
        em.persist(article);

        user.getArticles().add(article);
        em.persist(user);

        //when
        ResultActions perform = mockMvc.perform(
                get("/articles")
                        .param("category", "info")
                        .param("keyword", "JAVA")
                        .param("target", "tag")
                        .param("sort", "desc")
                        .param("page", "1")
                        .param("size", "1")
        );

        //then
        perform.andExpect(status().isOk());
    }
}
