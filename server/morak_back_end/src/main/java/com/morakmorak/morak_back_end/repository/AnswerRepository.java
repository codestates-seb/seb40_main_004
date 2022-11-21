package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByUserId(Long id);

    Page<Answer> findAllByArticleId(Long articleId, Pageable pageable);

    Optional<Answer> findAnswerByContent(String content);
}
