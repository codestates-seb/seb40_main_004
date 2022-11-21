package com.morakmorak.morak_back_end.repository.answer;

import com.morakmorak.morak_back_end.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findAnswerByContent(String content);
}
