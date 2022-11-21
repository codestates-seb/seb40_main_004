package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByUserId(Long id);
    Optional<Answer> findAnswerByContent(String content);
}
