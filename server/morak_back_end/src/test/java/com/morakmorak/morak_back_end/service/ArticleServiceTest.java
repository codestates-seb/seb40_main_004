package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.BookmarkDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Bookmark;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.repository.ArticleRepository;
import com.morakmorak.morak_back_end.repository.BookmarkRepository;
import com.morakmorak.morak_back_end.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;


import java.awt.print.Book;
import java.util.Optional;

import static com.morakmorak.morak_back_end.util.TestConstants.ID1;
import static com.morakmorak.morak_back_end.util.TestConstants.ID2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.Silent.class)
class ArticleServiceTest {
    @Mock
    BookmarkRepository bookmarkRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ArticleRepository articleRepository;

    @InjectMocks
    ArticleService articleService;

    /*@Test
    @DisplayName("북마크가 이미 등록되어 있다면, createBookmark 호출 시 북마크 삭제 로직 호출")
    void createBookmark_failed_1() {
    //given 북마크가 이미 등록되어 있음 (userId, articleId로 조회했을 때 bookmark 존재)
    User targetUser = User.builder()
            .id(ID1)
            .build();
    Article targetArticle =Article.builder()
            .id(ID2)
            .build();
    Bookmark dbBookmark = Bookmark.builder()
            .user(targetUser)
            .article(targetArticle)
            .build();
    BDDMockito.given(bookmarkRepository.findByUserIdAndArticleId(targetUser.getId(),targetArticle.getId())).willReturn(Optional.ofNullable(dbBookmark));

    //when createBookmark 호출시 북마크 삭제 로직이 호출된다
        BDDMockito.verify(articleService,times(1)).cancleBookmark(dbBookmark.getId());
    }*/

    @Test
    @DisplayName("북마크 저장 시 처음 저장하는 글이라면 repository.save()와 결과가 같다.")
    void createBookmark_failed_1() {
        //given 북마크가 이미 등록되어 있음 (userId, articleId로 조회했을 때 bookmark 존재)
        User targetUser = User.builder()
                .id(ID1)
                .build();
        Article targetArticle = Article.builder()
                .id(ID2)
                .build();
        Bookmark foundBookmark = null;


        BDDMockito.given(bookmarkRepository.findByUserIdAndArticleId(targetUser.getId(), targetArticle.getId())).willReturn(Optional.ofNullable(foundBookmark));

        //when createBookmark 호출시 북마크 저장 로직이 호출된다
        Long result = articleService.getBookmarkIdIfIsSaved(targetUser.getId(), targetArticle.getId());
        assertThat(result).isNegative();

    }

    @Test
    @DisplayName("북마크 저장 시 처음 저장하는 글이라면 repository.save()와 결과가 같다.")
    void pressBookmark_save_success1() {
        //given user, article, 둘과 연관관계 있는 bookmark 존재
        User targetUser = User.builder()
                .id(ID1)
                .build();
        Article targetArticle = Article.builder()
                .id(ID2)
                .build();
        Bookmark actualBookmark = Bookmark.builder().user(targetUser).article(targetArticle).build();

        //when
        lenient().doReturn(Optional.ofNullable(targetUser)).when(userRepository).findById(anyLong());
        lenient().doReturn(Optional.ofNullable(targetArticle)).when(articleRepository).findById(anyLong());
        lenient().doReturn(Optional.ofNullable(actualBookmark)).when(bookmarkRepository).save(actualBookmark);

//        Bookmark targetBookmark = articleService.saveBookmark(targetUser.getId(),targetArticle.getId());
        //then
        assertThat(articleService.saveBookmark(targetUser.getId(),targetArticle.getId())).isEqualTo(actualBookmark);
    }
}
    /*
     *컨트롤러에서 요청을 보낼 때 이미 헤더에 토큰이 있어야 한다 ->토큰이 있어야 들어오므로 테스트 안해도 됨 useId조회했는데 없으면
     * 북마크를 등록할 수 있는 기준
     * 1) 북마크 레파지토리에서 유저 기준으로 검색한다 bookmarkRepository.findByUserId()
     * 2) 유저의 북마크 리스트에서 아티클 id를 기준으로 검색
     * 3) 받아온 아티클 id가 없어야 한다
     * 북마크를 추가하면 유저의 북마크 리스트에 저장, 북마크에 아티클 저장
     */

