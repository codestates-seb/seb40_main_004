package com.morakmorak.morak_back_end.Integration.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.controller.UserController;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.*;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO : 테스트 시 매번 calendar 365개를 저장하는 방식이 너무 비효율적이다. 해당 부분에 대한 로직 자체를 검토하고 다시 활성화 예정.
@Transactional
@SpringBootTest(value = {
        "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy",
        "jwt.refreshKey=only_test_refresh_key_value_gn..rlfdlrkqnwhrgkekspdy"
})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "REDIS", matches = "redis")
public class UserTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserController userController;
    @Autowired
    EntityManager entityManager;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("LocalDate 형식이 유효하지 않을 경우 400 반환")
    void getUserActivityDetail_failed() throws Exception {
        //given
        User user = User.builder().build();
        userRepository.save(user);
        //when
        ResultActions perform = mockMvc.perform(get("/users/{user-id}/dashboard/activities/{date}", user.getId(), "20022-01-01"));

        //then
        perform.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("올해가 아닐 경우 416 반환")
    void getUserActivityDetail_failed2() throws Exception {
        //given
        User user = User.builder().build();
        userRepository.save(user);
        //when
        ResultActions perform = mockMvc.perform(get("/users/{user-id}/dashboard/activities/{date}", user.getId(), "2002-01-01"));

        //then
        perform.andExpect(status().isRequestedRangeNotSatisfiable());
    }

    @Test
    @DisplayName("활동 내역이 존재하지 않아도 예외가 발생하지 않는다.")
    void getUserActivityDetail_success() throws Exception {
        //given
        String date = LocalDate.now().toString();
        User user = User.builder().build();
        userRepository.save(user);
        //when
        ResultActions perform = mockMvc.perform(get("/users/{user-id}/dashboard/activities/{date}", user.getId(), date));

        //then
        perform.andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저가 작성한 아티클, 답변, 댓글에 대한 활동 내역을 반환한다.")
    void getUserActivityDetail_success2() throws Exception {
        //given
        String date = LocalDate.now().toString();
        User user = User.builder().build();
        userRepository.save(user);

        for (int i=0; i<5; i++) {
            Article article = Article.builder().title(CONTENT1).build();
            ArticleLike articleLike = ArticleLike.builder().article(article).user(user).build();
            Answer answer = Answer.builder().article(article).user(user).build();
            Comment comment = Comment.builder().article(article).content(CONTENT2).build();
            article.injectUserForMapping(user);
            answer.injectUser(user);
            comment.injectUser(user);

            entityManager.persist(article);
            entityManager.persist(articleLike);
            entityManager.persist(answer);
            entityManager.persist(comment);
        }

        //when
        ResultActions perform = mockMvc.perform(get("/users/{user-id}/dashboard/activities/{date}", user.getId(), date));

        //then
        perform.andExpect(status().isOk());
        perform.andExpect(jsonPath("$.articles[0].title", is(CONTENT1)));
        perform.andExpect(jsonPath("$.articles[0].likeCount", is(1)));
        perform.andExpect(jsonPath("$.articles[0].commentCount", is(1)));

        perform.andExpect(jsonPath("$.answers[0].title", is(CONTENT1)));
        perform.andExpect(jsonPath("$.answers[0].likeCount", is(1)));
        perform.andExpect(jsonPath("$.answers[0].commentCount", is(1)));

        perform.andExpect(jsonPath("$.comments[0].content", is(CONTENT2)));
        perform.andExpect(jsonPath("$.total", is(15)));
    }
}
