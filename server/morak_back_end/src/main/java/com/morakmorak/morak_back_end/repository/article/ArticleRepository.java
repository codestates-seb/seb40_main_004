package com.morakmorak.morak_back_end.repository.article;


import com.morakmorak.morak_back_end.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> , ArticleQueryRepository {
    Optional<Article> findByUserId(Long articleId);

    Optional<Article> findArticleByContent(String content);

    @Query("select a from Article a left join fetch a.user where a.id = :articleId")
    Optional<Article> findArticleRelationWithUser(Long articleId);
}
