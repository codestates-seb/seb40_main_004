package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Avatar;
import com.morakmorak.morak_back_end.entity.Bookmark;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class BookmarkRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @Test
    @DisplayName("checkUserBookmarked 메서드 정상 작동 테스트 ")
    public void checkUserBookmarked_test(){
    //given

        Avatar avatar = Avatar.builder().originalFileName("fileName").remotePath("remotePath").build();

        User user = User.builder().nickname("nickname").avatar(avatar)
                .email("test@naver.com").grade(Grade.BRONZE).build();
        Article article = Article.builder().user(user).build();
        user.getArticles().add(article);
        Bookmark bookmark = Bookmark.builder().user(user).article(article).memo("null").build();
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

}