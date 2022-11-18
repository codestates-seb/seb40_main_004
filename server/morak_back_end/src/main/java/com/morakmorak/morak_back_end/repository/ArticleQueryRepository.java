package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleQueryRepository {

    public Page<Article> search(String category, String keyword, String target, String sort, Pageable pageable);

}
