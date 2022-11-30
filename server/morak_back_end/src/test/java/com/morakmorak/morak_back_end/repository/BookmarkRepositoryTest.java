package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Avatar;
import com.morakmorak.morak_back_end.entity.Bookmark;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
class BookmarkRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @Test
    @DisplayName("checkUserBookmarked 메서드 정상 작동 테스트 ")
    public void checkUserBookmarked_test(){
    //given

        Avatar avatar = Avatar.builder().originalFilename("fileName").remotePath("remotePath").build();

        User user = User.builder().nickname("nickname").avatar(avatar)
                .email("test@naver.com").grade(Grade.BRONZE).build();
        Article article = Article.builder().user(user).build();
        user.getArticles().add(article);
        Bookmark bookmark = Bookmark.builder().user(user).article(article).memo("null").build();
        user.getBookmarks().add(bookmark);
        article.getBookmarks().add(bookmark);

        em.persist(avatar);
        em.persist(user);
        em.persist(article);
        em.persist(bookmark);

        //when
        Bookmark dbBookmark = bookmarkRepository.checkUserBookmarked(user.getId(), article.getId()).orElseThrow();

        //then
        assertThat(dbBookmark.getUser()).isEqualTo(user);
        assertThat(dbBookmark.getArticle()).isEqualTo(article);

    }

    @Test
    @DisplayName("인자값으로 들어온 게시글과 유저의 아이디로 저장된 북마크가 없을때 런타임 예외를 던진다.")
    public void checkUserBookmarked_fail(){
        //given

        Avatar avatar = Avatar.builder().originalFilename("fileName").remotePath("remotePath").build();

        User user = User.builder().nickname("nickname").avatar(avatar)
                .email("test@naver.com").grade(Grade.BRONZE).build();
        Article article = Article.builder().user(user).build();
        user.getArticles().add(article);

        Bookmark bookmark = Bookmark.builder().build();

        em.persist(avatar);
        em.persist(user);
        em.persist(article);
        em.persist(bookmark);

        //when
        //then
        assertThatThrownBy(() -> bookmarkRepository.checkUserBookmarked(user.getId(), article.getId()).orElseThrow())
                .isInstanceOf(RuntimeException.class );



    }

}