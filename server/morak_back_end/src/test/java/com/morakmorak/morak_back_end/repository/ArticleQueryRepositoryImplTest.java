package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.ArticleTag;
import com.morakmorak.morak_back_end.entity.Category;
import com.morakmorak.morak_back_end.entity.Tag;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.OrderBy;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Rollback(value = false)
class ArticleQueryRepositoryImplTest {

    @Autowired
    EntityManager em;

    @Autowired
    ArticleRepository articleRepository;


    @BeforeEach
    public void articleData() {
        List<ArticleTag> articleTags = new ArrayList<>();

        Tag JAVA = Tag.builder().name(TagName.JAVA).build();
        Tag C = Tag.builder().name(TagName.C).build();
        Category info = Category.builder().name("info").build();
        Category qna = Category.builder().name("qna").build();
        ArticleTag articleTagJAVA = ArticleTag.builder().tag(JAVA).build();
        ArticleTag articleTagC = ArticleTag.builder().tag(C).build();

        for (int i = 0; i < 10; i++) {

            Article article = Article.builder().title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~" + i)
                    .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                    .category(info)
                    .build();

            em.persist(article);
        }
        for (int i = 0; i < 10; i++) {

            Article article = Article.builder().title("테스트 타이틀입니다. 잘부탁드립니다. 제발 돼라!!!~~~~~~~~" + i)
                    .content("콘탠트입니다. 제발 됬으면 좋겠습니다.")
                    .category(qna)
                    .build();

            em.persist(article);
        }



    }

    @Test
    @DisplayName("게시글 조건없이 전체 페이징 조회 테스트")
    public void defaultPagingTest() throws Exception{
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);
        //when
        Page<Article> articles = articleRepository.search(null, null, null, null, pageRequest);
        //then
        assertThat(articles.getContent().size()).isSameAs(10);
        assertThat(articles.getSize()).isSameAs(10);
        assertThat(articles.getTotalPages()).isSameAs(2);
//        assertThat(articles.getContent().get(0).getArticleTags().get(0).getTag().getName()).isEqualTo(TagName.JAVA);
        assertThat(articles.getContent().get(3).getArticleTags().get(0).getTag().getName()).isEqualTo(TagName.C);
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

}