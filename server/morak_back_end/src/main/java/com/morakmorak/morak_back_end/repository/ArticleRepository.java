package com.morakmorak.morak_back_end.repository;


import com.morakmorak.morak_back_end.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleQueryRepository {

    Optional<Article> findArticleByContent(String content);
}
