package com.morakmorak.morak_back_end.repository.answer_repository;

import com.morakmorak.morak_back_end.config.JpaQueryFactoryConfig;
import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.repository.answer.AnswerQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static com.morakmorak.morak_back_end.util.TestConstants.NICKNAME1;
import static com.morakmorak.morak_back_end.util.TestConstants.NICKNAME2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(JpaQueryFactoryConfig.class)

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
        user_answer.injectTo(user);

        other_answer = Answer.builder().user(other).isPicked(false).article(user_article).build();
        other_answer.injectTo(other);

        picked_answer = Answer.builder().user(picked_user).isPicked(true).article(user_article).build();
        picked_answer.injectTo(picked_user);

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
    @Test
    @DisplayName("해당 게시글에 해당되는 답변만 필터링 확인")
    void getAnswersPickedFirst_count() {
        //given 해당 게시글에 해당되는 답변만 필터링된
        Article user_article_2 = Article.builder().user(user).articleStatus(ArticleStatus.POSTING).build();
        Answer other_answer_2 = Answer.builder().user(other).article(user_article_2).build();
        user_article_2.getAnswers().add(other_answer_2);

        PageRequest pageRequest = PageRequest.of(0, 5);
        Long articleId = user_article.getId();
        //when
        Page<Answer> target = answerQueryRepository.findAllByArticleId_PickedFirst(articleId, pageRequest);

        long total_count = target.getTotalElements();
        Answer answer_1 = target.getContent().get(0);
        Answer answer_2 = target.getContent().get(1);
        Answer answer_3 = target.getContent().get(2);

        //then
        assertThat(total_count).isEqualTo(3L);
        assertThat(answer_1.getArticle().getId()).isEqualTo(user_article.getId());
    }
    @Test
    @DisplayName("삭제된 글의 답변이 유저에게 조회되지 않음 확인")
    void findAnswersByUserId_posting_only() {
        //given
        Article user_article_deleted = Article.builder().user(user).articleStatus(ArticleStatus.REMOVED).build();
        Answer other_answer_2 = Answer.builder().user(other).article(user_article_deleted).build();
        user_article_deleted.getAnswers().add(other_answer_2);

        Long userId = other.getId();
        PageRequest pageRequest = PageRequest.of(0, 50);

        //when,then
        Page<Answer> target = answerQueryRepository.findAnswersByUserId(userId, pageRequest);
        List<Answer> other_answer_list = target.getContent();
        ArticleStatus article_status =other_answer_list.get(0).getArticle().getArticleStatus();

        Assertions.assertThat(other_answer_list.size()).isEqualTo(1);
        Assertions.assertThat(article_status).isEqualTo(ArticleStatus.POSTING);
    }
    @Test
    @DisplayName("유저 활동내역이 없어도 에러 발생하지 않음 확인")
    void findAnswersByUserId() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 50);
        Long userId = user.getId();
        user_article.changeArticleStatus(ArticleStatus.REMOVED);

        //when
        Page<Answer> target = answerQueryRepository.findAnswersByUserId(userId, pageRequest);
        List<Answer> user_answer_list = target.getContent();
        Assertions.assertThat(user_answer_list.size()).isEqualTo(0);
    }

}
