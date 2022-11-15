package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
    Optional<Bookmark> findByUserIdAndArticleId(Long userId, Long articleId);
    Optional<Bookmark> findByArticleId(Long id);
}
