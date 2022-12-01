package com.morakmorak.morak_back_end.repository.answer;

import com.morakmorak.morak_back_end.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long>, AnswerQueryRepository {
    Page<Answer> findByUserId(Long userId,Pageable pageable);


    Page<Answer> findAllByArticleId(Long articleId, Pageable pageable);

    Optional<Answer> findAnswerByContent(String content);

    Optional<Answer> findTopByUserId(Long id);

}
