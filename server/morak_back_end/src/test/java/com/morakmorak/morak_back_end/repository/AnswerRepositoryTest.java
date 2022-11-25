package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.repository.answer.AnswerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class AnswerRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    AnswerRepository answerRepository;

    @Test
    @DisplayName("findAnswerByContent 메서드 정상 작동 테스트")
    public void findAnswerByContent_suc(){
        //given
        Answer answer = Answer.builder().content("콘테트입니다. 잘부탁드립니다. 하하하하하").build();
        em.persist(answer);

        //when
        Answer dbAnswer = answerRepository.findAnswerByContent("콘테트입니다. 잘부탁드립니다. 하하하하하")
                .orElseThrow(() -> new RuntimeException("답변 없음"));
        //then
        assertThat(dbAnswer.getContent()).isEqualTo(answer.getContent());
    }
}