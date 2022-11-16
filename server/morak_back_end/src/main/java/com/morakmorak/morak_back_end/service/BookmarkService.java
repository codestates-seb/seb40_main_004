package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.BookmarkDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Bookmark;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.ArticleRepository;
import com.morakmorak.morak_back_end.repository.BookmarkRepository;
import com.morakmorak.morak_back_end.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;


    public BookmarkDto.ResponsePostBookmark pressBookmark(Long userId, Long articleId) {
        if (getBookmarkIdIfIsSaved(userId, articleId) != -1L) {
            cancleBookmark(getBookmarkIdIfIsSaved(userId, articleId));
            return BookmarkDto.ResponsePostBookmark.builder()
                    .userId(userId)
                    .articleId(articleId)
                    .scrappedByThisUser(false)
                    .build();
        } else {
            Bookmark createdBookmark = saveBookmark(userId, articleId);
            return BookmarkDto.ResponsePostBookmark.builder()
                    .userId(userId)
                    .articleId(articleId)
                    .scrappedByThisUser(true)
                    .createdAt(createdBookmark.getCreatedAt())
                    .lastModifiedAt(createdBookmark.getLastModifiedAt())
                    .build();
        }
    }

    public void cancleBookmark(Long bookmarkId) {
        bookmarkRepository.deleteById(bookmarkId);
    }


    public Long getBookmarkIdIfIsSaved(Long userId, Long articleId) {
        Optional<Bookmark> foundBookmark = bookmarkRepository.findByUserIdAndArticleId(userId, articleId);
        return foundBookmark.isPresent() ? foundBookmark.get().getId() : -1L;
    }

    public Bookmark saveBookmark(Long userId, Long articleId) {
        User verifiedUser = userRepository.findById(userId).orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        Article verifiedArticle = articleRepository.findById(articleId).orElseThrow(() -> new BusinessLogicException(ErrorCode.ARTICLE_NOT_FOUND));
        Bookmark createdBookmark = Bookmark.builder().user(verifiedUser)
                .article(verifiedArticle).build();
        verifiedUser.getBookmarks().add(createdBookmark);

        return bookmarkRepository.save(createdBookmark);
    }
}