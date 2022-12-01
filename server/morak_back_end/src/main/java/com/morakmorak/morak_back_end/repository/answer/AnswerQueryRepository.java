package com.morakmorak.morak_back_end.repository.answer;

import com.morakmorak.morak_back_end.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AnswerQueryRepository {

    Page<Answer> getAnswersPickedFirst(Long articleId, Pageable pageable);
    Page<Answer> findAnswersByUserId(Long userId,Pageable pageable);

}
