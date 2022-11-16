package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
