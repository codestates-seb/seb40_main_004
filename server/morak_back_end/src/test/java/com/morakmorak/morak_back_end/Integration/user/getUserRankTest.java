package com.morakmorak.morak_back_end.Integration.user;

import com.morakmorak.morak_back_end.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static com.morakmorak.morak_back_end.util.TestConstants.NICKNAME1;
import static com.morakmorak.morak_back_end.util.TestConstants.NICKNAME2;


public class getUserRankTest extends UserTest {
    private User user;
    private User other;

    @BeforeEach
    void init() {
        user = User.builder().nickname(NICKNAME1).point(100).build();
        other = User.builder().nickname(NICKNAME2).point(1000).build();

        Article article_user1 = Article.builder().user(user).build();
        Article article_user2 = Article.builder().user(user).build();

        article_user1.injectTo(user);
        article_user2.injectTo(user);

        Article article_other1 = Article.builder().user(other).build();
        article_other1.injectTo(other);

        Answer answer_other1 = Answer.builder().user(other).article(article_user1).build();
        answer_other1.injectTo(other);

        ArticleLike articleLike1_user = ArticleLike.builder().article(article_user1).user(other).build();
        ArticleLike articleLike2_user = ArticleLike.builder().article(article_user1).user(other).build();
        ArticleLike articleLike3_user = ArticleLike.builder().article(article_user1).user(other).build();
        ArticleLike articleLike4_user = ArticleLike.builder().article(article_user1).user(other).build();
        articleLike1_user.mapUserAndArticleWithLike();
        articleLike2_user.mapUserAndArticleWithLike();
        articleLike3_user.mapUserAndArticleWithLike();
        articleLike4_user.mapUserAndArticleWithLike();

        ArticleLike articleLike_other1 = ArticleLike.builder().article(article_other1).user(user).build();
        ArticleLike articleLike_other2 = ArticleLike.builder().article(article_other1).user(user).build();
        articleLike_other1.mapUserAndArticleWithLike();
        articleLike_other2.mapUserAndArticleWithLike();

        AnswerLike answerLike_user1 = AnswerLike.builder().answer(answer_other1).user(other).build();

        entityManager.persist(user);
        entityManager.persist(other);

        entityManager.persist(article_user1);
        entityManager.persist(article_user2);

        entityManager.persist(article_other1);

        entityManager.persist(answer_other1);

        entityManager.persist(articleLike_other1);
        entityManager.persist(articleLike_other2);

        entityManager.persist(articleLike1_user);
        entityManager.persist(articleLike2_user);
        entityManager.persist(articleLike3_user);
        entityManager.persist(articleLike4_user);

        entityManager.persist(answerLike_user1);
    }

//    TODO : Redis Cache 적용 이후 테스트 일관성이 사라진 문제로 인해 임시 주석처리
//    @Test
//    @DisplayName("유저 랭크 반환 테스트 1 _ 포인트순 조회시 other가 1위, 200 OK ")
//    void getUserRankTest1() throws Exception {
//        //given
//        //when
//        ResultActions perform = mockMvc.perform(get("/users/ranks?q=point&page=1&size=2")
//                .header("User-Agent", "Mozilla 5.0"));
//
//        //then
//        perform.andExpect(status().isOk())
//                .andExpect(jsonPath("$.data[0].userId", is(other.getId().intValue())))
//                .andDo(print());
//
//        Optional<User> byId = userRepository.findById(user.getId());
//
//        System.out.println(byId.get());
//    }
//
//    @Test
//    @DisplayName("유저 랭크 반환 테스트 2 _ 게시글 작성군 조회 시 user가 1위, 200 OK ")
//    void getUserRankTest2() throws Exception {
//        //given
//        //when
//        ResultActions perform = mockMvc.perform(get("/users/ranks?q=articles&page=1&size=2")
//                .header("User-Agent", "Mozilla 5.0"));
//
//        //then
//        perform.andExpect(status().isOk())
//                .andExpect(jsonPath("$.data[0].userId", is(user.getId().intValue())))
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("유저 랭크 반환 테스트 3 _ 답변순 조회 시 other가 1위, 200 OK ")
//    void getUserRankTest3() throws Exception {
//        //given
//        //when
//        ResultActions perform = mockMvc.perform(get("/users/ranks?q=answers&page=1&size=2")
//                .header("User-Agent", "Mozilla 5.0"));
//
//        //then
//        perform.andExpect(status().isOk())
//                .andExpect(jsonPath("$.data[0].userId", is(other.getId().intValue())))
//                .andDo(print());
//    }
}
