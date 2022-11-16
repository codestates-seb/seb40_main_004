package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
}
