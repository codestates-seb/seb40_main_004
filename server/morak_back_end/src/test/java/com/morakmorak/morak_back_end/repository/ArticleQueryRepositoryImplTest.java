package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
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

        Category info = Category.builder().name("info").build();
        Category qna = Category.builder().name("qna").build();
        em.persist(info);
        em.persist(qna);

        for (int i = 0; i < 10; i++) {

            ArticleTag articleTagJava = ArticleTag.builder().tag(JAVA).build();
            Article article = Article.builder().title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~" + i)
                    .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                    .articleTags(List.of(articleTagJava))
                    .category(info)
                    .vote(Vote.builder().build())
                    .build();
            info.getArticleList().add(article);
            articleTagJava.injectMappingForArticleAndTagForTesting(article);
            em.persist(article);

        }

        for (int i = 0; i < 10; i++) {

            ArticleTag articleTagC = ArticleTag.builder().tag(C).build();
            Article article = Article.builder().title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~" + i)
                    .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                    .category(qna)
                    .articleTags(List.of(articleTagC))
                    .vote(Vote.builder().build())
                    .build();
            qna.getArticleList().add(article);
            articleTagC.injectMappingForArticleAndTagForTesting(article);
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
        for (Article article : articles) {
            System.out.println("확인용");
            System.out.println(articles.getSize());
            System.out.println(articles.getTotalElements());
            String title = article.getTitle();
            System.out.println("title = " + title);
            article.getArticleTags().stream().forEach(articleTag ->
                    {
                        TagName name = articleTag.getTag().getName();
                        System.out.println("name = " + name);
                    }
            );
            System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
        }
    }

    @Test
    @DisplayName("게시글 태그검색 솔팅 페이징 조회 테스트 이게 되는거임!!!!!!!!!!!!!!!!!!!!!!!!!")
    public void tagSortingPagingTest() throws Exception{
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);
        //when
        Page<Article> articles = articleRepository.tagSearch("qna", "C", "tag", null, pageRequest);

        for (Article article : articles) {
            System.out.println("확인용");
            System.out.println(articles.getTotalElements());
            String title = article.getTitle();
            System.out.println("title = " + title);
            String categoryName = article.getCategory().getName();
            System.out.println("categoryName = " + categoryName);
            Integer likes = article.getVote().getCount();
            System.out.println("likes = " + likes);
            article.getArticleTags().stream().forEach(articleTag ->
            {
                TagName name = articleTag.getTag().getName();
                System.out.println("name = " + name);
            }
            );
            System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
        }

     }

     @Test
     @DisplayName("카테고리를 기준으로 검색을 할때 10개가 검색되는지와 info 카테고리만 검색 되는지 확인하기")
     public void categorySortingPagingTest() throws Exception{
         //given
         PageRequest pageRequest = PageRequest.of(0, 20);
         //when
         Page<Article> articles = articleRepository.search("qna", null, null, null, pageRequest);
         //then

         assertThat(articles.getContent().get(0).getCategory().getName()).isEqualTo("qna");
         assertThat(articles.getContent().get(9).getCategory().getName()).isEqualTo("qna");
         assertThat(articles.getContent().size()).isEqualTo(10);
      }

    @Test
    @DisplayName("컨텐트로 조회하기")
    public void contentSortingPagingTest() throws Exception{
        //given
        PageRequest pageRequest = PageRequest.of(0, 5);
        //when
        Page<Article> articles = articleRepository.search(null, "콘탠트입니다.", "content", null, pageRequest);
        //then
        assertThat(articles.getTotalPages()).isEqualTo(4);
        assertThat(articles.getTotalElements()).isEqualTo(20);
    }

    @Test
    @DisplayName("게시글 조건없이 카테고리가 조건이 info 일때 10개를 가지고 오는지 테스트")
    public void articleSortingTest() throws Exception{
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);
        //when
        Page<Article> articles = articleRepository.search("info", null, null, null, pageRequest);
        //then
        assertThat(articles.getContent().size()).isSameAs(10);
        assertThat(articles.getSize()).isSameAs(10);
        assertThat(articles.getTotalPages()).isSameAs(1);
    }
}