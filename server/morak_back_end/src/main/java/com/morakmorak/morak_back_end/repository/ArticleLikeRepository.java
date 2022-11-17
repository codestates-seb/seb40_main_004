package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    @Query("select a from ArticleLike a where a.user.id = :userId and a.article.id = :articleId")
    Optional<ArticleLike> checkUserLiked(Long userId, Long articleId);

}
