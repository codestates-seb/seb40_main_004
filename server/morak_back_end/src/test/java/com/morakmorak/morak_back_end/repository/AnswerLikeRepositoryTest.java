package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.repository.answer.AnswerLikeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)

class AnswerLikeRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    AnswerLikeRepository answerLikeRepository;

    @Test
    @DisplayName("checkUserLiked 메서드 정상작동 테스트")
    public void checkUserLiked_test(){
        //given
        Avatar avatar = Avatar.builder().originalFilename("fileName").remotePath("remotePath").build();

        User user = User.builder().nickname("nickname").avatar(avatar)
                .email("test@naver.com").grade(Grade.BRONZE).build();
        Answer answer = Answer.builder().user(user).build();
        AnswerLike answerLike = AnswerLike.builder().user(user).answer(answer).build();

        em.persist(avatar);
        em.persist(user);
        em.persist(answer);
        em.persist(answerLike);

        //when
        AnswerLike dbAnswerLike= answerLikeRepository.checkUserLiked(user.getId(), answer.getId()).orElseThrow();
        //then
        Assertions.assertThat(dbAnswerLike.getAnswer()).isEqualTo(answer);
        Assertions.assertThat(dbAnswerLike.getUser()).isEqualTo(user);
    }


}