package com.morakmorak.morak_back_end.repository.answer_repository;

import com.morakmorak.morak_back_end.config.JpaQueryFactoryConfig;
import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.repository.answer.AnswerQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static com.morakmorak.morak_back_end.util.TestConstants.NICKNAME1;
import static com.morakmorak.morak_back_end.util.TestConstants.NICKNAME2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(JpaQueryFactoryConfig.class)
//@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class AnswerQueryRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired
    JPAQueryFactory jpaQueryFactory;

    AnswerQueryRepository answerQueryRepository;

    User user;
    User other;
    User picked_user;
    Article user_article;
    Answer user_answer;
    Answer other_answer;
    Answer picked_answer;

    @BeforeEach
    void init() {
        answerQueryRepository = new AnswerQueryRepository(jpaQueryFactory);

        user = User.builder().nickname(NICKNAME1).grade(Grade.CANDLE).build();
        other = User.builder().nickname(NICKNAME2).grade(Grade.CANDLE).build();
        picked_user = User.builder().nickname("NICKNAME3").grade(Grade.CANDLE).build();

        user_article = Article.builder().user(user).isClosed(true).build();

        user_answer = Answer.builder().user(user).isPicked(false).article(user_article).build();
        user_answer.injectUser(user);

        other_answer = Answer.builder().user(other).isPicked(false).article(user_article).build();
        other_answer.injectUser(other);

        picked_answer = Answer.builder().user(picked_user).isPicked(true).article(user_article).build();
        picked_answer.injectUser(picked_user);

        user_article.getAnswers().add(user_answer);
        user_article.getAnswers().add(other_answer);
        user_article.getAnswers().add(picked_answer);


        em.persist(user);
        em.persist(other);
        em.persist(picked_user);

        em.persist(user_article);
        em.persist(user_answer);
        em.persist(other_answer);
        em.persist(picked_answer);
    }

    @Test
    @DisplayName("채택된 답변 상위노출 확인")
    void getAnswersPickedFirst() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 5);
        Long articleId = user_article.getId();

        Page<Answer> target = answerQueryRepository.findAllByArticleId_PickedFirst(articleId, pageRequest);

        Answer answer_1 = target.getContent().get(0);
        Answer answer_2 = target.getContent().get(1);
        Answer answer_3 = target.getContent().get(2);
        LocalDateTime answer2_time = answer_2.getCreatedAt();
        LocalDateTime answer3_time = answer_3.getCreatedAt();

        assertThat(answer_1.getIsPicked()).isEqualTo(true);
        assertThat(answer2_time).isAfter(answer3_time);
    }

}
