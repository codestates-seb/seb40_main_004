package com.morakmorak.morak_back_end.repository.answer;

import com.morakmorak.morak_back_end.entity.AnswerLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AnswerLikeRepository extends JpaRepository<AnswerLike, Long> {

    @Query("select a from AnswerLike a where a.user.id = :userId and a.answer.id = :answerId")
    Optional<AnswerLike> checkUserLiked(Long userId, Long answerId);
}
