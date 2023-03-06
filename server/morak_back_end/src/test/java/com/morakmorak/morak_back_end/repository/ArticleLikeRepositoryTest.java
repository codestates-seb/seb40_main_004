package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.repository.article.ArticleLikeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
class ArticleLikeRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    ArticleLikeRepository articleLikeRepository;

    @Test
    @DisplayName("checkUserLiked 메서드 정상작동 테스트")
    public void checkUserLiked_test(){
    //given
        Avatar avatar = Avatar.builder().originalFilename("fileName").remotePath("remotePath").build();

        User user = User.builder().nickname("nickname").avatar(avatar)
                .email("test@naver.com").grade(Grade.BRONZE).build();
        Article article = Article.builder().user(user).build();
        user.getArticles().add(article);
        ArticleLike articleLike = ArticleLike.builder().user(user).article(article).build();

        em.persist(avatar);
        em.persist(user);
        em.persist(article);
        em.persist(articleLike);

    //when
        ArticleLike dbArticleLike = articleLikeRepository.checkUserLiked(user.getId(), article.getId()).orElseThrow();
        //then
        Assertions.assertThat(dbArticleLike.getArticle()).isEqualTo(article);
        Assertions.assertThat(dbArticleLike.getUser()).isEqualTo(user);
    }
}