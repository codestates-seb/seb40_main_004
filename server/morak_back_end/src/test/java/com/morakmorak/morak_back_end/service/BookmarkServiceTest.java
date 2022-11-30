package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Bookmark;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.repository.article.ArticleRepository;
import com.morakmorak.morak_back_end.repository.BookmarkRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.morakmorak.morak_back_end.util.TestConstants.ID1;
import static com.morakmorak.morak_back_end.util.TestConstants.ID2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BookmarkServiceTest {
    @Mock
    BookmarkRepository bookmarkRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ArticleRepository articleRepository;

    @InjectMocks
    BookmarkService bookmarkService;

    @Test
    @DisplayName("북마크 저장 시 처음 저장하는 글이라면 getBookmarkIdIfIsSaved()의 결과가 -1이다.")
    public void createBookmark_failed_1() {
        //given 북마크가 등록돼있지 않음
        User targetUser = User.builder()
                .id(ID1)
                .build();
        Article targetArticle = Article.builder()
                .id(ID2)
                .build();
        Bookmark notSavedBookmark = Bookmark.builder()
                .user(targetUser)
                .article(targetArticle)
                .build();

        //when createBookmark 호출시 북마크 저장 로직이 호출된다
        BDDMockito.given(bookmarkRepository.findByUserIdAndArticleId(any(), anyLong())).willReturn(Optional.empty());

        Long result = bookmarkService.getBookmarkIdIfIsSaved(targetUser.getId(), targetArticle.getId());
        //then

        assertThat(result).isNegative();

    }

    @Test
    @DisplayName("북마크 저장 시 처음 저장하는 글이라면 repository.save()가 실행된다.")
    public void pressBookmark_save_success1() {
        //given user, article, 둘과 연관관계 있는 bookmark 존재
        User targetUser = User.builder()
                .id(ID1)
                .build();
        Article targetArticle = Article.builder()
                .id(ID2)
                .build();
        Bookmark notSavedBookmark = Bookmark.builder().user(targetUser).article(targetArticle).build();

        //when
        lenient().doReturn(Optional.ofNullable(targetUser)).when(userRepository).findById(anyLong());
        lenient().doReturn(Optional.ofNullable(targetArticle)).when(articleRepository).findById(anyLong());
        lenient().doReturn(notSavedBookmark).when(bookmarkRepository).save(any());

        bookmarkService.saveBookmark(ID1, ID2);

        //then

        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }
}