package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
class ArticleQueryRepositoryImplTest {

    @Autowired
    EntityManager em;

    @Autowired
    ArticleRepository articleRepository;


    @BeforeEach
    public void articleData() {

        Tag JAVA = Tag.builder().name(TagName.JAVA).build();
        Tag C = Tag.builder().name(TagName.C).build();
        em.persist(JAVA);
        em.persist(C);

        Category info = Category.builder().name(CategoryName.INFO).build();
        Category qna = Category.builder().name(CategoryName.QNA).build();
        em.persist(info);
        em.persist(qna);

        List<ArticleLike> articleLikes = new ArrayList<>();
        ArticleLike like = ArticleLike.builder().build();
        em.persist(like);
        articleLikes.add(like);
        for (int i = 0; i < 10; i++) {
            Category category;
            if (i % 2 == 0) {
                category = info;
            } else {
                category = qna;
            }
            ArticleTag articleTagJava = ArticleTag.builder().tag(JAVA).build();
            Article article = Article.builder().title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~" + i)
                    .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                    .articleTags(List.of(articleTagJava))
                    .category(category)
                    .articleLikes(articleLikes)
                    .build();
            info.getArticleList().add(article);
            articleTagJava.injectMappingForArticleAndTag(article);
            em.persist(article);
        }

        for (int i = 0; i < 10; i++) {

            Category category;
            if (i % 2 == 0) {
                category = info;
            } else {
                category = qna;
            }

            ArticleTag articleTagC = ArticleTag.builder().tag(C).build();
            Article article = Article.builder().title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~" + i)
                    .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                    .category(category)
                    .articleTags(List.of(articleTagC))
                    .articleLikes(articleLikes)
                    .build();
            qna.getArticleList().add(article);
            articleTagC.injectMappingForArticleAndTag(article);
            em.persist(article);
        }


    }

    @Test
    @DisplayName("게시글 조건없이 전체 페이징 조회 테스트")
    public void ltPagingTest() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0, 20);
        //when
        Page<Article> articles = articleRepository.search(null, null, null, null, pageRequest);
        //then
        assertThat(articles.getTotalElements()).isEqualTo(20);
        assertThat(articles.getSize()).isSameAs(20);
        assertThat(articles.getTotalPages()).isSameAs(1);
    }

    @Test
    @DisplayName("게시글 태그검색 솔팅 페이징 조회 테스트 이게 되는거임!!!!!!!!!!!!!!!!!!!!!!!!!")
    public void tagSortingPagingTest() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);
        //when
        Page<Article> articles = articleRepository.search(null, "C", "tag", null, pageRequest);


        assertThat(articles.getTotalElements()).isEqualTo(10);
        assertThat(articles.getSize()).isSameAs(10);
        assertThat(articles.getTotalPages()).isSameAs(1);

    }

    @Test
    @DisplayName("카테고리를 기준으로 검색을 할때 10개가 검색되는지와 info 카테고리만 검색 되는지 확인하기")
    public void categorySortingPagingTest() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0, 20);
        //when
        Page<Article> articles = articleRepository.search("QNA", null, null, null, pageRequest);
        //then

        assertThat(articles.getContent().get(0).getCategory().getName()).isEqualTo(CategoryName.QNA);
        assertThat(articles.getContent().get(9).getCategory().getName()).isEqualTo(CategoryName.QNA);
        assertThat(articles.getContent().size()).isEqualTo(10);
    }

    @Test
    @DisplayName("컨텐트로 조회하기")
    public void contentSortingPagingTest() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);
        //when
        Page<Article> articles = articleRepository.search(null, "콘탠트입니다.", "content", "like-desc", pageRequest);
        //then
        assertThat(articles.getTotalPages()).isEqualTo(2);
        assertThat(articles.getTotalElements()).isEqualTo(20);
        assertThat(articles.getSize()).isSameAs(10);


    }

    @Test
    @DisplayName("게시글 조건없이 카테고리가 조건이 info 일때 10개를 가지고 오는지 테스트")
    public void articleSortingTest() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);
        //when
        Page<Article> articles = articleRepository.search("INFO", null, null, null, pageRequest);
        //then
        assertThat(articles.getContent().size()).isSameAs(10);
        assertThat(articles.getSize()).isSameAs(10);
        assertThat(articles.getTotalPages()).isSameAs(1);
    }

}